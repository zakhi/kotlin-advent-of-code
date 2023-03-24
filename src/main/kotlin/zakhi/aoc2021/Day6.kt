package zakhi.aoc2021

import zakhi.helpers.entireTextOf


fun main() {
    val input = entireTextOf("aoc2021/day6").trim().split(",").map { it.toLong() }

    val totalAfter80Days = input.sumOf { totalFishCount(bornAt = it - 7, atDay = 80)  }
    println("The number of fish at day 80 is $totalAfter80Days")

    val totalAfter256Days = input.sumOf { totalFishCount(bornAt = it - 7, atDay = 256) }
    println("The number of fish at day 80 is $totalAfter256Days")
}


private fun totalFishCount(bornAt: Long, atDay: Long): Long {
    val countCache = mutableMapOf<Long, Long>()

    fun fishCount(bornAt: Long, atDay: Long): Long = countCache.getOrPut(bornAt) {
        val offset = if (bornAt > 0) 2 else 0

        generateSequence(bornAt + offset) { it + 7 }.drop(1)
            .takeWhile { it < atDay }
            .sumOf { childBornAt -> fishCount(childBornAt, atDay) + 1 }
    }

    return fishCount(bornAt, atDay) + 1
}
