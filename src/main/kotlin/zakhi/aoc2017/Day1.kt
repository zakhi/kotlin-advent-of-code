package zakhi.aoc2017

import zakhi.helpers.entireTextOf


fun main() {
    val input = entireTextOf("aoc2017/day1").trim()

    val sum = input.filterByIndex { input.indicesMatch(it, it + 1) }.sumOf { it.digitToInt() }
    println("The repeating digits sum is $sum")

    val otherSum = input.filterByIndex { input.indicesMatch(it, it + input.length / 2) }.sumOf { it.digitToInt() }
    println("The repeating digits other sum is $otherSum")
}


private fun String.filterByIndex(predicate: (Int) -> Boolean): String =
    filterIndexed { index, _ -> predicate(index) }

private fun String.indicesMatch(firstIndex: Int, secondIndex: Int): Boolean =
    this[firstIndex.mod(length)] == this[secondIndex.mod(length)]
