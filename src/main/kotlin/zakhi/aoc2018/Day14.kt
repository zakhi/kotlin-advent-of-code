package zakhi.aoc2018

import zakhi.helpers.entireTextOf
import zakhi.helpers.join


fun main() {
    val input = entireTextOf("aoc2018/day14").trim().toInt()

    val recipes = Recipes()
    val scores = recipes.getScores(10, after = input)
    println("The scores are ${scores.join()}")

    val newRecipes = Recipes()
    val scoreCount = newRecipes.countScoresBefore(input)
    println("The number of score before the sequence is $scoreCount")
}


private class Recipes {
    private val scores = mutableListOf(3, 7)
    private var first = 0
    private var second = 1

    fun getScores(count: Int, after: Int): List<Int> {
        while (scores.size < count + after) {
            makeRecipes()
        }

        return scores.subList(after, after + count)
    }

    fun countScoresBefore(sequence: Int): Int {
        val sequenceDigits = sequence.digits()

        while (true) {
            findScores(sequenceDigits, offset = -1)?.let { return it }
            findScores(sequenceDigits, offset = 0)?.let { return it }
            makeRecipes()
        }
    }

    private fun makeRecipes() {
        val newScores = (scores[first] + scores[second]).digits()
        scores.addAll(newScores)

        first = (first + scores[first] + 1).mod(scores.size)
        second = (second + scores[second] + 1).mod(scores.size)
    }

    private fun findScores(sequence: List<Int>, offset: Int): Int? {
        val startIndex = scores.size - sequence.size + offset
        if (startIndex < 0) return null

        return if (scores.subList(startIndex, startIndex + sequence.size) == sequence) startIndex else null
    }

    private fun Int.digits(): List<Int> = toString().map { it.digitToInt() }
}
