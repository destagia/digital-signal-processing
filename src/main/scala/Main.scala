package util

import scala.math._

object Main {

	def main (args:Array[String]) {
		println("Hello World")
		val sc = new SoundCreater(8000,0.5,6000,500)
		sc.makeSound("hoge3.raw", (A, f0, samp_freq, n) => {
			A * sin(2.0 * Pi * f0 * n / samp_freq)
		})

		sc.makeSound("kukei.raw", (A, f0, samp_freq, n) => {
			if (n.toInt%10 < 5) {
				A
			} else {
				-A
			}
		})

	}



}