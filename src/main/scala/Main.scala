package jp.kobe.util

import scala.math._
import java.io._

object Main extends App{

	implicit def seq2list[A](seq:Seq[A]):List[A] = seq.toList

	val sampFreq = 8000 // サンプリング周波数
	val span = 0.032 // 短時間フーリエ変換の一回の長さ
	val time = 10 // 音声の長さ
	val frame_span = sampFreq * span toInt // １フレームのサンプリング数

	// データを読み込む
	val yall = Tools.readFile("resources/noisy_song.dat", (sampFreq * time).toInt)
	println("[dsp] finished reading file.")

	// frame_span区切りのリストのリストを作成する
	def makep(l:List[Float]):List[List[Float]] = l match {
		case Nil => Nil
		case _ => 
			if (l.length >= frame_span) l.take(frame_span) :: makep(l.drop(frame_span))	
			else Nil
	}

	val p = makep(yall.take(sampFreq)) // 音声リストから１秒分とってくる
	println("[dsp] finsihed make p list.")	
	println(p)

	val P = p.map{ pi => 
		val dft = DFT.transform(pi.map(f => inum(f, 0)), pi.size)
		println(dft)
		dft.reduceLeft(_ + _)
	}
	println("[dsp] made P.")
	println(P)

	val Pnorm = P.map(_.magnitude).sum / P.size

	val Y = DFT.transform(yall.map(yi => inum(yi, 0)), yall.size)
	println("[dsp] calculated Y from y.")

	val S = for (i <- (0 to Y.size-1)) yield {
		val diff = Y(i).magnitude - Pnorm
		DFT.expj(Y(i).phase) * (if (diff < 0) Y(i).magnitude*0.01 else diff)
	}
	println("[dsp] calculated S from y and P.")

	val s = DFT.retransform(S, S.size).map(_.magnitude.toFloat)
	println("[dsp] retransformed s from S.")

	Tools.makeFileFromList("not_noisy.raw", s)
}















