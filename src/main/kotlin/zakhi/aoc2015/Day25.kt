package zakhi.aoc2015

import zakhi.entireTextOf
import zakhi.naturalNumbers


fun main() {
    val match = Regex("""row (\d+), column (\d+)""").find(entireTextOf("aoc2015/day25")) ?: throw Exception("invalid input")
    val (row, column) = match.groupValues.drop(1).map { it.toInt() }

    val rowStart = 1 + naturalNumbers().take(row - 1).sum()
    val cell = rowStart + naturalNumbers(from = row + 1).take(column - 1).sum()

    val modulo = 33554393
    val code = 20151125L * 252533L.pow(cell - 1, modulo) % modulo
    println("The code for the machine is $code")
}

private fun Long.pow(exponent: Int, modulo: Int): Long {
    return (1 until exponent).fold(this) { current, _ -> current * this % modulo }
}
