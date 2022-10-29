package zakhi.aoc2020

import zakhi.helpers.charList
import zakhi.helpers.entireTextOf


fun main() {
    val input = entireTextOf("aoc2020/day6").trim().split("\n\n").map { group -> group.lines() }

    val yesByAnyone = input.sumOf { group -> group.flatMap { it.charList() }.distinct().count() }
    println("The sum of the counts of anyone is $yesByAnyone")

    val yesByEveryone = input.sumOf { group ->
        val counts = group.flatMap { it.charList() }.groupingBy { it }.eachCount()
        counts.count { it.value == group.size }
    }

    println("The sum of counts of everyone is $yesByEveryone")
}
