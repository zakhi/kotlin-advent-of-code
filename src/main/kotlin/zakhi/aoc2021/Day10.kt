package zakhi.aoc2021

import zakhi.helpers.linesOf
import zakhi.helpers.push


fun main() {
    val lines = linesOf("aoc2021/day10").toList()

    val errorScores = lines.associateWith { errorScoreOf(it) }
    println("The syntax error total score is ${errorScores.values.sum()}")

    val autoCompleteScores = errorScores.filter { it.value == 0 }.keys.map { autoCompleteScoreOf(it) }.sorted()
    println("The middle auto-complete score is ${autoCompleteScores[autoCompleteScores.size / 2]}")
}


private fun errorScoreOf(line: String): Int {
    val stack = mutableListOf<Char>()

    for (char in line) {
        if (char in matches.values)
            stack.push(char)
        else if (matches[char] != stack.removeLastOrNull())
            return errorScores.getValue(char)
    }

    return 0
}

private fun autoCompleteScoreOf(line: String): Long {
    val stack = mutableListOf<Char>()

    for (char in line) {
        if (char in matches.values) stack.push(char) else stack.removeLast()
    }

    return stack.foldRight(0) { char, score ->
        score * 5 + autoCompleteScores.getValue(char)
    }
}

private val matches = mapOf(')' to '(', ']' to '[', '}' to '{', '>' to '<')
private val errorScores = matches.keys.zip(listOf(3, 57, 1197, 25137)).toMap()
private val autoCompleteScores = matches.values.mapIndexed { i, char -> char to i + 1 }.toMap()
