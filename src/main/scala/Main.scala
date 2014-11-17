package util

import scala.math._

object Main {

	def main (args:Array[String]) {

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

		val list3 = sc.fadeOut(sc.makeEffectWave(0.2, 0.6, 880, 440))
		sc.makeFile("effect.raw", list3)

		val list4 = sc.foldCalc(list3)
		sc.makeFile("fold.raw", list4)

	}
}








