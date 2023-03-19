package zakhi.aoc2021

import zakhi.helpers.Point
import zakhi.helpers.entireTextOf
import zakhi.helpers.x
import zakhi.helpers.y


fun main() {
    val input = entireTextOf("aoc2021/day4").trim().split("\n\n")
    val drawnNumbers = input.first().split(",").map { it.toInt() }
    val game = Bingo(input.drop(1))

    for (number in drawnNumbers) {
        game.mark(number)
    }

    println("The first winning board score is ${game.firstWinningBoard?.winningScore}")
    println("The last winning board score is ${game.lastWinningBoard?.winningScore}")
}


private class Bingo(boardTexts: List<String>) {
    private val playingBoards = boardTexts.map { it.toBoard() }
    private val winningBoards = mutableSetOf<BingoBoard>()

    val firstWinningBoard get() = winningBoards.firstOrNull()
    val lastWinningBoard get() = winningBoards.lastOrNull()

    fun mark(number: Int) {
        playingBoards.forEach { board ->
            board.mark(number)
            if (board.winner) winningBoards.add(board)
        }
    }
}

private class BingoBoard(private val numberPositions: Map<Int, Point>) {
    private val markedPositions = mutableSetOf<Point>()

    val winner: Boolean get() = winningScore != null

    var winningScore: Int? = null
        private set

    fun mark(number: Int) {
        if (winner) return

        numberPositions[number]?.let { markedPositions.add(it) }
        checkWinBy(number)
    }

    private fun checkWinBy(number: Int) {
        val columnMarks = markedPositions.groupingBy { it.x }.eachCount()
        val rowMarks = markedPositions.groupingBy { it.y }.eachCount()

        if (columnMarks.containsValue(5) || rowMarks.containsValue(5)) {
            winningScore = numberPositions.filterValues { it !in markedPositions }.keys.sum() * number
        }
    }
}

private fun String.toBoard() = BingoBoard(split("\n").flatMapIndexed { y, line ->
    line.trim().split(Regex("""\s+""")).mapIndexed { x, num ->
        num.toInt() to (x to y)
    }
}.toMap())
