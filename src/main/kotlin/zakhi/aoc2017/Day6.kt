package zakhi.aoc2017

import zakhi.helpers.entireTextOf


fun main() {
    val input = Regex("""\d+""").findAll(entireTextOf("aoc2017/day6")).map { it.value.toInt() }.toList()
    val memory = Memory(input)
    memory.redistributeUntilDuplicateFound()

    println("The number of redistributions is ${memory.redistributionsDone}")
    println("The size of the loop is ${memory.loopSize}")
}


private class Memory(initialBanks: List<Int>) {
    private val banks = initialBanks.toMutableList()
    private val redistributions = LinkedHashSet(listOf(initialBanks))

    fun redistributeUntilDuplicateFound() {
        while (true) {
            val maxBlocks = banks.maxOrNull() ?: throw Exception("Banks are empty")
            val topBank = banks.indexOf(maxBlocks)

            banks[topBank] = 0
            (1..maxBlocks).forEach { i -> banks[(topBank + i).mod(banks.size)] += 1 }

            if (banks in redistributions) return
            redistributions.add(banks.toList())
        }
    }

    val redistributionsDone: Int get() = redistributions.size
    val loopSize: Int get() = redistributionsDone - redistributions.indexOf(banks)
}
