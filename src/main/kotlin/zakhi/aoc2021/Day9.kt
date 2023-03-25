package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val grid = linesOf("aoc2021/day9").flatMapIndexed { y, line ->
        line.mapIndexed { x, char -> (x to y) to char.digitToInt() }
    }.toMap()

    val lowPoints = grid.filter { (point, value) ->
        point.adjacentNeighbors.mapNotNull { grid[it] }.all { it > value }
    }

    val riskLevel = lowPoints.values.sumOf { it + 1 }
    println("The risk level of all low points is $riskLevel")

    val threeLargestBasins = lowPoints.keys.map { grid.basinSizeOf(it) }.sortedDescending().take(3)
    println("The product of the three largest basin sizes are ${threeLargestBasins.product()}")
}


private fun Map<Point, Int>.basinSizeOf(point: Point): Int {
    val basin = mutableSetOf(point)
    val pointsToCheck = mutableListOf(point)

    while (pointsToCheck.isNotEmpty()) {
        val next = pointsToCheck.removeFirst()
        val nextValue = getValue(next)

        val neighbors = next.adjacentNeighbors.filter { get(it) in (nextValue + 1) ..< 9 }
        pointsToCheck.addAll(neighbors.filterNot { it in basin })
        basin.addAll(neighbors)
    }

    return basin.size
}
