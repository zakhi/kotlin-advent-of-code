package zakhi.aoc2021

import zakhi.helpers.fail
import zakhi.helpers.join
import zakhi.helpers.linesOf


fun main() {
    val numbers = linesOf("aoc2021/day3").toList()

    val gammaRate = numbers.first().indices.map { i -> numbers.mostCommonAt(i) }.join()
    val epsilonRate = gammaRate.map { it.opposite }.join()
    val powerConsumption = gammaRate.toInt(2) * epsilonRate.toInt(2)
    println("The power consumption is $powerConsumption")

    val oxygenGeneratorRating = numbers.search(mostCommon = true)
    val co2ScrubberRating = numbers.search(mostCommon = false)
    val lifeSupportRating = oxygenGeneratorRating.toInt(2) * co2ScrubberRating.toInt(2)
    println("The life support rating is $lifeSupportRating")
}


private fun List<String>.mostCommonAt(index: Int): Char {
    val (ones, zeros) = partition { it[index] == '1' }
    return if (zeros.size > ones.size) '0' else '1'
}

private fun List<String>.search(mostCommon: Boolean): String {
    val left = toMutableList()

    for (i in first().indices) {
        val mostCommonBit = left.mostCommonAt(i)
        val expectedBit = if (mostCommon) mostCommonBit else mostCommonBit.opposite
        left.removeIf { it[i] != expectedBit }

        if (left.size == 1) break
    }

    return left.first()
}

private val Char.opposite get() = when (this) {
    '0' -> '1'
    '1' -> '0'
    else -> fail("Invalid bit $this")
}
