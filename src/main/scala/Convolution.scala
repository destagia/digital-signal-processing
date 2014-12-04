package jp.kobe.util

object Convolution{
	def calc (list:List[Float], h:List[Float]) = {
		def makeList(x:List[Float], h:List[Float], res:List[Float]):List[Float] = {
			if (x.isEmpty || h.isEmpty) res
			else makeList(x, h.init, List(calcOne(x,h.reverse)) ++ res)
		}
		makeList(list, h, Nil)
	}

	def calcOne(x:List[Float], h:List[Float]) = {
		def for1 (x0:List[Float], h0:List[Float], res1:Float):Float = {
				if (x0.isEmpty || h0.isEmpty) res1
				else for1 (x0.tail, h0.tail, (res1 + (x0.head * h0.head)).toShort)
		}
		for1(x, h, 0)
	}
}