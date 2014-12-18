package jp.kobe.util

import scala.math._
import java.io._

object Main extends App{

	implicit def seq2list[A](seq:Seq[A]):List[A] = seq.toList
	implicit def floatlist2inumlist(list:List[Float]):List[inum] = list.map(a => inum(a, 0))

	val sampFreq = 8000 // サンプリング周波数
	val span = 0.032 // 短時間フーリエ変換の一回の長さ
	val time = 10 // 音声の長さ
	val frame_span = sampFreq * span toInt // １フレームのサンプリング数

	// データを読み込む
	val yall = Tools.readFile("resources/noisy_song.dat", (sampFreq * time).toInt)
	println("[dsp] finished reading file.")
	val K = yall.size

	// frame_span区切りのリストのリストを作成する
	def makep[A](l:List[A]):List[List[A]] = l match {
		case Nil => Nil
		case _ => 
			if (l.length >= frame_span) l.take(frame_span) :: makep[A](l.drop(frame_span))	
			else Nil
	}

	val y = makep[Float](yall)
	val Y = y.map { yi =>
		DFT.transform(yi, K)
	}
	//val Y = DFT.transform(yall.map(yi => inum(yi, 0)), yall.size)
	println("[dsp] calculated Y from y. " + Y.size + ", " + y.size)

	val p = y.head // 音声リストから１秒分とってくる
	println("[dsp] finsihed make p list.")	
	println(p)

	val P = DFT.transform(p, K)
	println("[dsp] made P.")

	def Pnorm(k:Int) = Y.take(31).map(Yl => Yl(k).magnitude).sum / 31.0f

	val S = for (Yl <- Y) yield {
		for (omega  <- (0 to Yl.size-1)) yield {
			val diff = Yl(omega).magnitude - Pnorm(omega)
			DFT.expj(Yl(omega).phase) * (if (diff < 0) Yl(omega).magnitude*0.01 else diff)
		}
	}
	println("[dsp] calculated S from y and P. " + S.size)
	// S.map(println) 
	// val s = DFT.retransform(S, S.size).map(_.magnitude.toFloat)
	val s = S.map { Si => 
		DFT.retransform(Si, K)
	}.reduceLeft(_ ++ _).map(_.re.toShort)

	println("[dsp] retransformed s from S. " + s.size)

	Tools.makeFile("not_noisy.raw", s)
}















