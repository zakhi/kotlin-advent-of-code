package zakhi.aoc2022

import zakhi.aoc2022.HandShape.*
import zakhi.helpers.cyclicNextFrom
import zakhi.helpers.cyclicPreviousFrom
import zakhi.helpers.fail
import zakhi.helpers.matchEachLineOf


fun main() {
    val input = matchEachLineOf("aoc2022/day2", Regex("""(\w) (\w)""")) { (a, b) -> a.first() to b.first() }

    val totalScore = input.map { handShapeOf(it.first) to handShapeOf(it.second) }
        .sumOf { (opponent, me) -> me.handScore + me.playScore(against = opponent) }

    println("The total score is $totalScore")

    val actualScore = input.map { handShapeOf(it.first) to handShapeOf(it.first).handShapeResultingIn(it.second) }
        .sumOf { (opponent, me) -> me.handScore + me.playScore(against = opponent) }

    println("The actual score is $actualScore")
}


private enum class HandShape(val handScore: Int) {
    Rock(1),
    Paper(2),
    Scissors(3);

    fun playScore(against: HandShape): Int = when (against) {
        winsOverShape -> 6
        losesToShape -> 0
        else -> 3
    }

    val winsOverShape get() = values().toList().cyclicPreviousFrom(ordinal)
    val losesToShape get() = values().toList().cyclicNextFrom(ordinal)
}

private fun handShapeOf(char: Char): HandShape = when (char) {
    'A', 'X' -> Rock
    'B', 'Y' -> Paper
    'C', 'Z' -> Scissors
    else -> fail("Invalid hand shape: $char")
}

private fun HandShape.handShapeResultingIn(char: Char): HandShape = when (char) {
    'X' -> winsOverShape
    'Y' -> this
    'Z' -> losesToShape
    else -> fail("Invalid hand shape result: $char")
}
