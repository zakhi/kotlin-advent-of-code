package zakhi.aoc2016

import zakhi.helpers.wholeNumbers


fun main() {
    val input = 643 * 4
    val number = wholeNumbers().first { isClockSignal(it + input) }

    println("The lowest number to generate a clock signal is $number")
}


private fun isClockSignal(i: Int): Boolean {
    if (i == 0) return true
    if (i.mod(4) != 2) return false

    return isClockSignal(i / 4)
}
