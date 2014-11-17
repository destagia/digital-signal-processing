package util

import scala.math._

object Main {

	def main (args:Array[String]) {

		val sc = new SoundCreater(8000,0.5,6000,500)

		val list1 = sc.makeSound((A, f0, samp_freq, n) => {
			A * sin(2.0 * Pi * f0 * n / samp_freq)
		})

		sc.makeFile("sin.raw", list1)

		sc.makeSound((A, f0, samp_freq, n) => {
			val t0 = 1.0 / f0
			if (0 <= n && n < (t0 * samp_freq)/2.0) {
				A
			} else if ((t0 * samp_freq)/2.0 <= n && n < t0 * samp_freq) {
				-A
			} else {
				0
			}
		})

	}
}