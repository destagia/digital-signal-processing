package jp.kobe.util

import java.util.Random
import scala.math._

class ScaleAnnotation(name:String, count:Int) {
	implicit def Short2Float(s:Short):Float = s.toFloat
	implicit def Double2Float(d:Double):Float = d.toFloat
	implicit def Vector2List[A](v:Vector[A]):List[A] = v.toList

	println("[dsp] init Scale Annotation")
	val random = new Random(1)

	val samp_freq = 8000         // サンプリング周波数8000
	val frame_rate = 0.032       // 0.032秒おきに短時間フーリエ変換
	val frame_span = samp_freq * frame_rate toInt

	val all_sounds = Tools.readBinary("resources/7/all_sounds.raw")
	println("[dsp] load all_sounds.raw")

	val data = Tools.readBinary("resources/7/" + name)
	println("[dsp] load " + name)

	val I = samp_freq * frame_rate toInt       // 行列Xの行数
	val J = (data.size / I).toInt                // 行列Xの列数
	println("[dsp] I : " + I + " J : " + J)

	// frame_span区切りのリストのリストを作成する
	def makeShortTimeList[A](l:List[A], res:List[List[A]]):List[List[A]] = l match {
		case Nil => Nil
		case _ => 
			if (l.length >= frame_span) makeShortTimeList[A](l.drop(frame_span), res ++ List(l.take(frame_span)))
			else res
	}
	println("[dsp] divide list for short DFT")

	// 各値に対して，DFTを行う。
	val XT = makeShortTimeList(data, Nil).par.map { x => 
		DFT.transform(x.map(y => inum(y, 0)), 1, frame_span).par.map(_.magnitude.toFloat).toArray
	}.toArray

	val WT = makeShortTimeList(all_sounds, Nil).par.map { x => 
		DFT.transform(x.map(y => inum(y, 0)), 1, frame_span).par.map(_.magnitude.toFloat).toArray
	}.toArray

	println("XT = (" + XT.size + "x" + XT(0).size + ")")
	println("WT = (" + WT.size + "x" + WT(0).size + ")")

	val K = WT.size          // Wの列数 または Hの行数
	val sound_span = K/18    // 音階1つのフレームの長さ(W行列の列数ともいえる) 

	println("[dsp] K : " + K + ", sound_span : " + sound_span)
	val H0 = (for (k <- (0 to K-1)) yield {     // 0~1の小数の乱数が入ったHの適当な初期値
		(for (j <- (0 to J-1)) yield {
			random.nextFloat() * 0.1f
		}).toArray
	}).toArray

	println("[dsp] made H0 list")
	println("[dsp] calclating H ..")
	def calcH(H:Array[Array[Float]], times:Int):(Array[Array[Float]], List[Float]) = {
		val ratio = ((1.0f - times.toFloat / count.toFloat) * 100).toInt
		print("\r")
		print("[" + (0 until 100).map(x => if (x < ratio) "#" else " ").reduceLeft(_ + _) + "]")

		// WH行列はW行列とH行列の積なので，あらかじめ計算しておく
		val WH = 
		(0 to I-1).par.map { i =>
			(0 to J-1).par.map { j =>
				(0 to K-1).par.map { k =>
					WT(k)(i) * H(k)(j)
				}.sum
			}.toArray
		}.toArray

		// 次のHを計算する
		val nextH = (0 to K-1).par.map { a =>
			(0 to J-1).par.map { u =>

				var sum1 = 
				(0 to I-1).par.map { i =>
					WT(a)(i) * XT(u)(i) / WH(i)(u)
				}.sum

				var sum2 = 
				(0 to I-1).par.map { k =>
					WT(a)(k)
				}.sum

				val el = H(a)(u) * sum1 / sum2
				if (el >= 0) el
				else 0

			}.toArray
		}.toArray

		// KL情報量の計算
		val D:Float =
		(0 until I).par.map { i =>
			(0 until J).par.map { j => 
				val value:Float = XT(j)(i) * log(XT(j)(i).toDouble / (WH(i)(j).toDouble)) - XT(j)(i) + WH(i)(j)
				if (value.isNaN) 0
				else value
			}.sum
		}.sum

		times match {
			case 1 => (nextH, Nil)
			case _ => 
				val (h, d) = calcH(nextH, times-1)
				(h, List(D) ++ d)
		}
	}
	val res = calcH(H0, count)
	val H = res._1
	val D = res._2


	Tools.makeFileFromList("KL.dat", D)
	
	// すべての音階に対して，データを作成
	for (a <- (0 to 17)) yield {
		val sound = for (j <- (0 to J-1)) yield {
			val point = a * sound_span
			(point until point+sound_span).map { b =>
				H(b)(j)
			}.sum

		}	
		Tools.makeFileFromList(a + "_sound.dat", sound.toList)
	}
	

}
