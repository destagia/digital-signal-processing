package jp.kobe.util

import java.io._
import java.lang.Long

object Tools {

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

	def makeFileFromList (name:String, list:List[Float]) {
		val file = new File(name)
		val pw = new PrintWriter(new BufferedWriter(new FileWriter(file)))
		var count = 0
		for (f <- list) {
			println(f)
			pw.println(f)
			count += 1
		}
		pw.close()
	}

	def readFile(fileName:String, limit:Int = 10000000):List[Float] = {
		val file = new File(fileName)
		val fr = new FileReader(file)
		val br = new BufferedReader(fr)

		def read(br0:BufferedReader, res:List[Float], l:Int):List[Float] = {
			val str = br0.readLine()

			if (str == null || l > limit) {
				res
			} else {
				val value = 
					if (str != "nan") str.toFloat 
					else 30000
				read(br0, res ++ List(value), l+1)
			}
		}

		read(br, Nil, 0)
	}

}