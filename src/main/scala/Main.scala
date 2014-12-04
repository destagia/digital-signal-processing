package jp.kobe.util

import scala.math._

object Main {

	def main (args:Array[String]) {
		/*
		val sc = new SoundCreater(16000,0.6,6000,500)

		val list1 = sc.makeSound((A, f0, samp_freq, n) => {
			A * sin(2.0 * Pi * f0 * n / samp_freq)
		})
		sc.makeFile("sin.raw", list1)

		val list2 = sc.makeSound((A, f0, samp_freq, n) => {
			val t0 = 1.0 / f0
			val maxN = (t0 * samp_freq).toInt
			val m = (n.toInt % maxN).toDouble
			if (0 <= m && m < (t0 * samp_freq)/2.0) {
				A
			} else if ((t0 * samp_freq)/2.0 <= m && m < t0 * samp_freq) {
				-A
			} else {
				0
			}
		})
		sc.makeFile("kukeiha.raw", list2)

		val list3 = sc.makeEffectWave(0.2, 0.6, 880, 440)
		val list5 = sc.fadeOut(list3)
		sc.makeFile("effect.raw", list5)

		val list4 = sc.foldCalc(list5.map(_.toFloat), sc.readImpulse())
		sc.makeFile("fold.raw", list4.map(_.toShort))

		val soundCreater = new SoundCreater(44100, 0.5, 6000, 500)
		
		val effect = soundCreater.fadeOut(soundCreater.makeEffectWave(0.2, 0.5, 880, 440))
		val numList = effect.map { a =>
			inum(a.toFloat, 0.0f)
		}

		val impulse = soundCreater.readFile("resources/impulse44.dat")
		
		val M = numList.size + impulse.size - 1
		
		val X = DFT.transform(numList,M)
		val H = DFT.transform(impulse.map(a => inum(a, 0)),M)
		val Y = DFT.multiple(X, H)
		val y = DFT.retransform(Y,M)
Z
		soundCreater.makeFile("effect_with_dft.raw", y.map(a => a.re.toShort))
		val soundCreater = new SoundCreater(12000,0.6,440,880)
		val filter = soundCreater.readFile("resources/filter.dat")
		val oto = soundCreater.readFile("resources/oto.dat")

		val otoN = oto.size
		val OTO = DFT.transform(oto.map(x => inum(x, 0)), otoN)	
		val OTOre = OTO.map(_.re)

		val effect = soundCreater.fadeOut(soundCreater.makeEffectWave(0.2, 0.5, 880, 440))
		val numList = effect.map { a =>
			inum(a.toFloat, 0.0f)
		}

		val M = filter.size + numList.size - 1

		val X = DFT.transform(numList,M)
		// Tools.makeFileFromList("oto_hz.dat", X.map(_.magnitude.toFloat))
		val H = DFT.transform(filter.map(x => inum(x, 0)), M)
		
		val Y = DFT.multiple(X, H)
		val y = DFT.retransform(Y, M)
		Tools.makeFileFromList("Y_hz.dat", Y.map(_.magnitude.toFloat))
		soundCreater.makeFile("y.raw", y.map(_.re.toShort))		

		*/
		Tools.makeFile("error.raw", NLMS.eList.map(_.toShort))
	}
}















