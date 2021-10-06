package zakhi.aoc2018

import zakhi.helpers.linesOf


fun main() {
    val changes = linesOf("aoc2018/day1").map { it.toInt() }
    val resultFrequency = changes.sum()
    println("The result frequency is $resultFrequency")

    val encounteredFrequencies = mutableSetOf<Int>()
    val firstRepeated = allFrequencies(changes).first { !encounteredFrequencies.add(it) }
    println("The first repeated frequency is $firstRepeated")
}


private fun allFrequencies(changes: Sequence<Int>) =
    sequence { while (true) yieldAll(changes) }.runningFold(0, Int::plus)
