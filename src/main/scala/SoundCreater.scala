package jp.kobe.util
import java.io._
import scala.math._
import java.lang.Long

class SoundCreater(
	val samp_freq:Double, // サンプリング周波数
	val time:Double, // 音声の時間
	val A:Double, // 振幅
	val f0:Double // 基本周波数
	)
{

	val listLength:Double = time * samp_freq

	// Short型をそのままバイナリに書き込んでも，C言語と同じバイナリにならなかった。
	// おそらく，unsigedとかが絡んでるけど，よくわからんからごり押し。
	def fetch (s:Short) = {
		val strTemp = "%04x".format(s)
		val str =
			if (strTemp.length() != 4) {
				strTemp.replace("ffff", "")
			} else {
				strTemp
			}

		val str2 = str(2).toString + str(3).toString + str(0).toString + str(1).toString
		Long.parseLong(str2, 16).toShort
	}

	def makeFile (name:String, list:List[Short]) {
		// ファイル書き込みオブジェクトを作成
		val outputFile = new File("resources/" + name)
		val fos = new FileOutputStream(outputFile)
		val dos = new DataOutputStream(fos)

		list.foreach((value) => {
			dos.writeShort(fetch (value))
		})

		dos.close()
	}

	def makeSound(function:(Double,Double,Double,Double) => Double):List[Short] = {
		(for (i <- (0 to listLength.toInt-1)) yield {
			val n = i.toDouble
			function(A, f0, samp_freq, n).toShort
		}).toList
	}

	def readBinary(fileName:String):List[Float] = {
		val inputFile = new File(fileName)
		val fis = new FileInputStream(inputFile)
		val dis = new DataInputStream(fis)

		(for (i <- (0 to listLength.toInt-1)) yield {
			dis.readFloat()
		}).toList
	}

	def readFile(fileName:String) = {
		val file = new File(fileName)
		val fr = new FileReader(file)
		val br = new BufferedReader(fr)

		def read(br0:BufferedReader, res:List[Float]):List[Float] = {
			val str = br0.readLine()

			if (str == null) {
				res
			} else {
				read(br0, res ++ List(str.toFloat))
			}
		}

		read(br, Nil)
	}


	def foldCalc (list:List[Float], h:List[Float]) = {
		def makeList(x:List[Float], h:List[Float], res:List[Float]):List[Float] = {
			def calc (x0:List[Float], h0:List[Float], res1:Float):Float = {
				if (x0.isEmpty || h0.isEmpty) res1
				else calc (x0.tail, h0.tail, (res1 + (x0.head * h0.head)).toShort)
			}

			if (x.isEmpty || h.isEmpty) res
			else makeList(x, h.init, List(calc(x,h.reverse,0)) ++ res)
		}
		makeList(list, h, Nil)
	}

	def makeEffectWave (start:Double, end:Double, maxFreq:Double, minFreq:Double) = {
		val fList = makeSound((A, f0, samp_freq, n) => {
			val length0 = samp_freq * start
			val length = samp_freq * end

			if (n < length0) {
				minFreq.toShort
			} else {
				val m = n - length0
				(m * (maxFreq - minFreq) / (length - length0) + minFreq).toShort
			}
		})

		def makeWave(f0:Double, n:Int):List[Short] = {
			val t0n = 1.0 / f0
			val maxN = (t0n * samp_freq).toInt
			val nextf = fList(
				if (n + maxN < listLength) { (n + maxN).toInt }
				else { (listLength-1).toInt })

			val list =
			(for (m <- (0 to maxN)) yield {
				if (0 <= m && m < (t0n * samp_freq)/2.0) {
					A
				} else if ((t0n * samp_freq)/2.0 <= m && m < t0n * samp_freq) {
					-A
				} else {
					0
				}
			}).toList.map(_.toShort)
			val addList =
				if (n < listLength) makeWave(nextf, n + maxN)
				else List[Short]()
			list ++ addList
		}
		makeWave(fList.head, 0)
	}

	def fadeOut(list:List[Short]) = {
		(for (i <- (0 to list.size-1)) yield {
			(-(1.0 / (list.size-1).toDouble) * i.toDouble + 1.0)* list(i)
		}).toList.map(_.toShort)
	}

}