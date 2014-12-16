package jp.kobe.util

import scala.math._
import java.io._

object Main {

	implicit def seq2list[A](seq:Seq[A]):List[A] = seq.toList

	def main (args:Array[String]) {
		val sampFreq = 8000
		val span = 0.032
		val time = 5
		val frame_span = sampFreq * span toInt
		val yall = Tools.readFile("resources/noisy_song.dat", (sampFreq * time).toInt)

		def makep(l:List[Float]):List[List[Float]] = {
			l match {
				case Nil => Nil
				case _ => l.take(frame_span) :: makep(l.drop(frame_span))
			}
		}

		val p = makep(yall.take(sampFreq))
		val P = p.map(pi => DFT.transform(pi.map(f => inum(f, 0)), pi.size).reduceLeft(_ + _))
		val Pnorm = P.map(_.magnitude).sum / P.size
		val Y = DFT.transform(yall.map(yi => inum(yi, 0)), yall.size)

		val S = for (i <- (0 to Y.size-1)) yield {
			val diff = Y(i).magnitude- Pnorm
			DFT.expj(Y(i).phase) * (if (diff < 0) 0.01 else diff)
		}

		val s = DFT.retransform(S, S.size).map(_.magnitude.toFloat)

		Tools.makeFileFromList("not_noisy.raw", s)
	}
}















