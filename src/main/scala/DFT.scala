package jp.kobe.util

import scala.math._

case class inum(re:Float, im:Float) {
	def magnitude = {
		sqrt(pow(re,2.0) + pow(im,2.0))
	}

	def phase = {
		atan2(re.toDouble, im.toDouble)
	}

	def *(that:inum):inum = {
		inum((this.re * that.re) - (this.im * that.im), (this.re * that.im) + (this.im * that.re))
	}

	def *(that:Double):inum = {
		inum(this.re*that toFloat, this.im*that toFloat)
	}

	def +(that:inum):inum = {
		inum(this.re + that.re, this.im + that.im)
	}

	def /(that:Float):inum = {
		inum(this.re/that, this.im/that)
	}

	override def toString() = {
		"imargin number (Re: " + re + ", Im: " + im + ")"
	}
}

object DFT {

	// e^jNをオイラーの公式より，複素数に変換します。
	def expj (N:Double) = {
		inum(cos(N).toFloat,sin(N).toFloat)
	}

	def transform (x:List[inum],M:Double, N:Double):List[inum] = {
		def transformIn(n:Int, res:List[inum]):List[inum] = {
			def calc (x1:List[inum], m:Int, res1:inum):inum = {
				if (x1.isEmpty) {
					res1
				} else {
					val value = x1.head * expj(-n.toDouble * (2.0*Pi/N) * m.toDouble)
					calc(x1.tail, m+1, res1 + value)
				}
			}

			if (n >= N) {
				res
			} else {
				transformIn(n+1, res ++ List(calc(x, M.toInt, inum(0, 0))))
			}
		}
		transformIn(0, Nil)
	}

	def retransform (X:List[inum], M:Double, N:Double):List[inum] = {
		def retransIn(n:Int, res:List[inum]):List[inum] = {
			def calc(x1:List[inum], m:Int, res1:inum):inum = {
				if (x1.isEmpty) {
					res1
				} else {
					val value = x1.head * expj(n.toDouble * (2.0 * Pi / N) * m.toDouble)
					calc(x1.tail, m+1, res1 + value)
				}
			}

			if (n >= N) {
				res
			} else {
				retransIn(n+1, res ++ List(calc(X, M.toInt, inum(0,0)) / N.toFloat))
			}
		}
		retransIn(0, Nil)
	}

	def multiple(a:List[inum], b:List[inum]) = {
		def calc(x:List[inum], y:List[inum], res:List[inum]):List[inum] = {
			if (x.isEmpty || y.isEmpty) {
				res
			} else {
				calc(x.tail, y.tail, res ++ List(x.head * y.head))
			}
		}

		calc(a, b, Nil)
	}

}