package jp.kobe.util

import java.util.Random
import scala.math._

class ScaleAnnotation(name:String) {
	implicit def Short2Float(s:Short):Float = s.toFloat
	implicit def Vector2List[A](v:Vector[A]):List[A] = v.toList

	println("[dsp] init Scale Annotation")
	val random = new Random(1)

	val samp_freq = 8000         // サンプリング周波数8000
	val frame_rate = 0.032       // 0.032秒おきに短時間フーリエ変換
	val frame_span = samp_freq * frame_rate toInt

	val all_sounds = Tools.readBinary("resources/7/all_sounds.raw")
	println("[dsp] load all_sounds.raw")
	// all_sounds.map(println(_))
	val data = Tools.readBinary("resources/7/" + name)
	// data.map(println(_))
	// println(data.size)
	println("[dsp] load " + name)
	Tools.makeFile("re_em_chord.raw", data)

	val I = samp_freq * frame_rate toInt       // 行列Xの行数
	val J = (data.size / I).toInt                // 行列Xの列数
	println("[dsp] I : " + I + " J : " + J)
	// frame_span区切りのリストのリストを作成する
	def makeShortTimeList[A](l:List[A]):List[List[A]] = l match {
		case Nil => Nil
		case _ => 
			if (l.length >= frame_span) l.take(frame_span) :: makeShortTimeList[A](l.drop(frame_span))
			else Nil
	}
	println("[dsp] divide list for short DFT")

	val XT = makeShortTimeList(data).par.map { x => 
		DFT.transform(x.map(x => inum(abs(x), 0)), x.size, frame_span).toArray
	}.toArray

	val WT = makeShortTimeList(all_sounds).par.map { x => 
		DFT.transform(x.map(inum(_, 0)), x.size, frame_span).toArray
	}.toArray

	println("XT = (" + XT.size + "x" + XT(0).size + ")")
	println("WT = (" + WT.size + "x" + WT(0).size + ")")

	val K = WT.size    // Wの列数 または Hの行数

	val H0 = (for (k <- (0 to K-1)) yield {     // 0~1の小数の乱数が入ったHの適当な初期値
		(for (j <- (0 to J-1)) yield {
			random.nextFloat() * 0.1f
		}).toArray
	}).toArray
	println("[dsp] made H0 list")
	
	def calcH(H:Array[Array[Float]], times:Int):Array[Array[Float]] = {
		val WH = 
		(for (i <- (0 to I-1)) yield { 
			(for (j <- (0 to J-1)) yield {
				(for (k <- (0 to K-1)) yield {
					WT(k)(i).re * H(k)(j)
				}).toList.sum
			}).toArray
		}).toArray

		val nextH = (for (a <- (0 to K-1)) yield {
			(for (u <- (0 to J-1)) yield {

				var sum1 = inum(0,0)
				for (i <- (0 to I-1)) {
					sum1 += WT(a)(i) * XT(u)(i) / WH(i)(u)
				}

				var sum2 = 0.0f
				for (k <- (0 to I-1)) {
					sum2 += WT(a)(k).re
				}

				val el = H(a)(u) * sum1.re / sum2
				if (el >= 0) el
				else 0
			}).toArray
		}).toArray

		times match {
			case 1 => nextH
			case _ => calcH(nextH, times-1)
		}
	}
	val H = calcH(H0, 10)
	println("H = (" + H.size + "x" + H.head.size + ")")
	
	for (a <- (0 to (K/J).toInt-1)) yield {
		val sound = for (j <- (0 to J-1)) yield {
			var sum = 0.0f
			val point = a * J
			for (b <- (point to point+52)) {
				sum += H(b)(j)
			}
			sum
		}	
		// Tools.makeFileFromList(a + "_sound.dat", sound.toList)
	}
	

}
