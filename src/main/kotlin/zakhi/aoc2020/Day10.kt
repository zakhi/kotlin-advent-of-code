package zakhi.aoc2020

import zakhi.helpers.linesOf


fun main() {
    val input = linesOf("aoc2020/day10").map { it.toInt() }.toList()

    val adapters = (listOf(0, input.max() + 3) + input).sorted()
    val diffCounts = adapters.zipWithNext().map { (a, b) -> b - a }.groupingBy { it }.eachCount()

    println("The differences product is ${diffCounts.getValue(1) * (diffCounts.getValue(3))}")

    val arrangements = mutableListOf(1L)

    adapters.indices.drop(1).forEach { current ->
        val count = (current - 3 until current)
            .filter { it >= 0 && adapters[current] - adapters[it] <= 3 }
            .sumOf { arrangements[it] }

        arrangements.add(count)
    }

    println("The total number of ways is ${arrangements.last()}")
}
