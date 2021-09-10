package zakhi.aoc2017

import zakhi.aoc2017.StreamState.*
import zakhi.helpers.entireTextOf

fun main() {
    val stream = entireTextOf("aoc2017/day9").trim()
    val processor = StreamProcessor(stream)
    processor.start()

    println("The total score of groups is ${processor.totalScore}")
    println("The total garbage is ${processor.totalGarbage}")
}


private class StreamProcessor(private val stream: String) {
    private var state = Group
    private var currentGroupScore = 0

    var totalScore = 0
        private set

    var totalGarbage = 0
        private set

    fun start() {
        stream.forEach { char ->
            when (state) {
                Group -> processInGroup(char)
                Garbage -> processInGarbage(char)
                Escaped -> state = Garbage
            }
        }
    }

    private fun processInGroup(char: Char) {
        when (char) {
            '{' -> currentGroupScore += 1
            '<' -> state = Garbage
            '}' -> {
                totalScore += currentGroupScore
                currentGroupScore -= 1
            }
        }
    }

    private fun processInGarbage(char: Char) {
        when (char) {
            '>' -> state = Group
            '!' -> state = Escaped
            else -> totalGarbage += 1
        }
    }
}

private enum class StreamState {
    Group,
    Garbage,
    Escaped
}
