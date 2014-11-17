package util
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
		val outputFile = new File(name)
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

}