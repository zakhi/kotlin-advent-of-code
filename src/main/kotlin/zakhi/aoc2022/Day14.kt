package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val rocks = buildSet {
        linesOf("aoc2022/day14").forEach { line ->
            line.split(" -> ").map { point -> point.split(",").map { it.toInt() }.toPoint() }.zipWithNext().forEach { (start, end) ->
                val xRange = if (start.x < end.x) start.x..end.x else end.x..start.x
                val yRange = if (start.y < end.y) start.y..end.y else end.y..start.y
                grid(xRange, yRange).forEach { point -> add(point) }
            }
        }
    }

    val sandFlow = SandFlow(rocks)
    sandFlow.flow(500 to 0)
    println("The number of tiles with sand at rest is ${sandFlow.sandAtRest}")

    val floorSandFlow = SandFlow(rocks, floor = rocks.maxOf { it.y } + 2)
    floorSandFlow.flow(500 to 0)
    println("The number of tiles with sand at rest with floor is ${floorSandFlow.sandAtRest}")
}


private class SandFlow(
    rocks: Set<Point>,
    private val floor: Int? = null
) {
    private val map = rocks.associateWith { Tile.ROCK }.toMutableMap()
    private val abyss = rocks.maxOf { it.y } + 1

    val sandAtRest get() = map.values.count { it == Tile.SAND }

    fun flow(start: Point) {
        while (true) {
            val endPosition = moveSandUnit(start)

            if (!endPosition.inAbyss()) map[endPosition] = Tile.SAND
            if (endPosition == start || endPosition.inAbyss()) return
        }
    }

    private fun moveSandUnit(start: Point): Point {
        var current = start

        while (!current.inAbyss()) {
            val newPosition = listOf(0 to 1, -1 to 1, 1 to 1).map { current + it }.firstOrNull { !it.isOccupied() }
            if (newPosition == null) break
            current = newPosition
        }

        return current
    }

    private fun Point.isOccupied(): Boolean = y == floor || this in map
    private fun Point.inAbyss() = floor == null && y == abyss
}

private enum class Tile {
    ROCK, SAND
}
