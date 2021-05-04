package zakhi.aoc2015

import zakhi.Point
import zakhi.input.entireTextOf
import zakhi.numbers.isOdd
import zakhi.points.plus


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


private class Route {
    private val positions = mutableListOf(0 to 0)

    fun move(move: Char) {
        positions.add(positions.last() + offsetOf(move))
    }

    val visitedPositions: Set<Point> get() = positions.toSet()
}


private fun offsetOf(move: Char): Point = when (move) {
    '^' -> 0 to 1
    '>' -> 1 to 0
    'v' -> 0 to -1
    '<' -> -1 to 0
    else -> throw Exception("Illegal move $move")
}
