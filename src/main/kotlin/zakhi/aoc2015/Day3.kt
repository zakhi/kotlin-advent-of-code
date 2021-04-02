package zakhi.aoc2015

import zakhi.entireTextOf


fun main() {
    val directions = entireTextOf("aoc2015/day3")

    val santaRoute = Route()
    directions.forEach { santaRoute.move(it) }

    println("Santa visited ${santaRoute.visitedPositions.size} houses")

    val santaSecondRoute = Route()
    val roboSantaRoute = Route()

    directions.forEachIndexed { index, direction ->
        if (index.isOdd) santaSecondRoute.move(direction) else roboSantaRoute.move(direction)
    }

    val totalVisitedPositions = santaSecondRoute.visitedPositions + roboSantaRoute.visitedPositions
    println("Santa and Robo-Santa visited ${totalVisitedPositions.size} houses")
}


private typealias Position = Pair<Int, Int>

private class Route {

    private var currentPosition = 0 to 0
    private val positions = mutableSetOf(currentPosition)

    fun move(move: Char) {
        currentPosition += offsetOf(move)
        positions.add(currentPosition)
    }

    val visitedPositions: Set<Position> = positions
}


private val Int.isOdd get() = this % 2 != 0

private fun offsetOf(move: Char): Position = when (move) {
    '^' -> 0 to 1
    '>' -> 1 to 0
    'v' -> 0 to -1
    '<' -> -1 to 0
    else -> throw Exception("Illegal move $move")
}

private operator fun Position.plus(offset: Position) = first + offset.first to second + offset.second
