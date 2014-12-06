package jp.kobe.util

object NLMS {
	val length = 8000 * 50

	println("load x and d")
	val x:List[Float] = Tools.readFile("resources/song2.dat")
	val d:List[Float] = Tools.readFile("resources/observed_song2.dat")

	val alpha = 0.02f
	val K = 80 // Kは何次かを表す。

	// 長さがKのh[0]のみを持ったリストのリストh
	println("make first h")
	var eList:List[Float] = Nil
	var h:List[List[Float]] = List((for(i <- (1 to K)) yield 0.0f).toList)
	println("make all h")
	makeFilterCoefficient()

	// 誤差e(n)である。関数になってる。
	def e(n:Int, h:List[Float]):Float = {
		val error = d(n) - Convolution.calcOne(x, h)
		eList = eList ++ List(error)
		error
	}

	def normx(n:Int):Float = {
		def for1(k:Int, res:Float):Float = {
			k match {
			case 0 => 0.0f
			case _ =>
				val m = n-k
				for1(k-1, res + getX(m) * getX(m))
			}
		}
		val value = for1(K-1, 0)

		if (value < 0.5f) {
			1
		} else {
			value
		}
	}

	def getX(n:Int) = {
		if (n < 0) 0
		else x(n)
	}

	def makeFilterCoefficient() {

		def for1(n:Int, N:Int, prevH:List[Float], res:List[List[Float]]):List[List[Float]] = {
			def for2(k:Int, K:Int, res:List[Float]):List[Float] = {
				k match {
					case K => res
					case _ => for2(k+1, K, res ++ List(prevH(k) + (alpha / normx(n)) * e(n, prevH) * getX(n-k)))
				}
			}
			n match {
				case N => res
				case _ =>
					val next = for2(0, K, Nil)
					for1(n+1, N, next, res ++ List(next))
			}
		}

		val count:Int = x.size / 1000

		def makeList(i:Int) {
			if (i < count) {
				println(i + " / " + count)
				h = h ++ for1(0, 1000, h.last, Nil)
				makeList(i + 1)
			}
		}
		makeList(0)
	}

}