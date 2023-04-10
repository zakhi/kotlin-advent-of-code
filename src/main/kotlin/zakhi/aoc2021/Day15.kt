package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val grid = linesOf("aoc2021/day15").flatMapIndexed { y, line ->
        line.mapIndexed { x, char -> (x to y) to char.digitToInt() }
    }.toMap()

    val dimension = grid.maxOf { it.key.x } + 1

    val risk = minimalDistance(0 to 0, isTarget = { it == dimension - 1 to dimension - 1 }) { point ->
        point.adjacentNeighbors.filter { it.inGrid(dimension) }.map { it to grid.getValue(it) }
    }

    println("The total risk is $risk")

    val newDimension = dimension * 5

    val actualRisk = minimalDistance(0 to 0, isTarget = { it == newDimension - 1 to newDimension - 1 }) { point ->
        point.adjacentNeighbors.filter { it.inGrid(newDimension) }.map {
            it to (grid.getValue(it.mod(dimension)) + (it / dimension).gridDistance).wrap()
        }
    }

    println("The total actual risk is $actualRisk")
}


private fun Point.inGrid(size: Int) = x in 0 ..< size && y in 0 ..< size

private fun Int.wrap() = this.mod(9).let { if (it == 0) 9 else it }
