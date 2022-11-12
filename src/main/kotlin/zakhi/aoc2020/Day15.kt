package zakhi.aoc2020

import zakhi.helpers.entireTextOf


fun main() {
    val input = entireTextOf("aoc2020/day15").trim().split(",").map { it.toInt() }
    val game = MemoryGame(input)

    while (game.totalNumbersSaid < 2020) game.sayNextNumber()
    println("The 2020th number is ${game.lastNumberSaid}")

    while (game.totalNumbersSaid < 30000000) game.sayNextNumber()
    println("The 30000000th number is ${game.lastNumberSaid}")
}


private class MemoryGame(seed: List<Int>) {
    private val lastOccurrences = seed.dropLast(1).withIndex().associate { (index, number) -> number to index + 1 }.toMutableMap()

    var totalNumbersSaid = seed.size
        private set

    var lastNumberSaid = seed.last()
        private set

    fun sayNextNumber() {
        val newNumber = lastOccurrences[lastNumberSaid]?.let { totalNumbersSaid - it } ?: 0
        lastOccurrences[lastNumberSaid] = totalNumbersSaid
        lastNumberSaid = newNumber
        totalNumbersSaid += 1
    }
}
