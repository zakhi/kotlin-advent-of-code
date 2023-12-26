package zakhi.aoc2022

import zakhi.helpers.entireTextOf


fun main() {
    val elfCalories = entireTextOf("aoc2022/day1").trim().split("\n\n").map { section ->
        section.lines().map { it.toInt() }
    }

    val sortedElfCalories = elfCalories.sortedByDescending { it.sum() }

    val highestCalories = sortedElfCalories.first().sum()
    println("The most calories carried by an elf is $highestCalories")

    val topThreeCalories = sortedElfCalories.take(3).flatten().sum()
    println("The total calories of the top 3 elves is $topThreeCalories")
}
