package zakhi.aoc2017

import zakhi.helpers.linesOf
import zakhi.helpers.mapDestructured
import zakhi.helpers.pairs


fun main() {
    val lines = linesOf("aoc2017/day2").map { line ->
        Regex("""\d+""").findAll(line).map { it.value.toInt() }.sortedDescending().toList()
    }

    val checksum = lines.sumOf { it.first() - it.last() }
    println("The checksum is $checksum")

    val results = lines.sumOf { values ->
        val (high, low) = values.pairs().first { (a, b) -> a % b == 0 }
        high / low
    }

    println("The sum of results is $results")
}
