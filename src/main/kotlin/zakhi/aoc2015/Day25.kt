package zakhi.aoc2015

import zakhi.input.matchEntireTextOf
import zakhi.numbers.naturalNumbers


fun main() {
    val (row, column) = matchEntireTextOf("aoc2015/day25", Regex("""row (\d+), column (\d+)""")) { (row, column) ->
        row.toInt() to column.toInt()
    }

    val rowStart = 1 + naturalNumbers().take(row - 1).sum()
    val cell = rowStart + naturalNumbers(from = row + 1).take(column - 1).sum()

    val modulo = 33554393
    val code = 20151125L * 252533L.pow(cell - 1, modulo) % modulo
    println("The code for the machine is $code")
}

private fun Long.pow(exponent: Int, modulo: Int): Long {
    return (1 until exponent).fold(this) { current, _ -> current * this % modulo }
}
