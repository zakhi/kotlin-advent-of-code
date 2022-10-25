package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.math.pow


fun main() {
    val initialLocations = linesOf("aoc2019/day24").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') BugLocation(x to y) else null }
    }.toSet()

    val encounteredLocations = mutableSetOf<Set<BugLocation>>()

    val repeatedLocations = wholeNumbers().runningFold(initialLocations) { current, _ ->
        transformSingleLevel(current)
    }.dropWhile { encounteredLocations.add(it) }.first()

    println("The rating of the first repeated bug locations is ${repeatedLocations.rating}")

    val finalLocations = (1..200).fold(initialLocations) { current, _ -> transform(current) }
    println("After 200 minutes, there are ${finalLocations.size} bugs")
}


private data class BugLocation(
    val point: Point,
    val level: Int = 0
) {
    val sameLevelNeighbors get() = point.adjacentNeighbors.map { BugLocation(it, level) }

    val multiLevelNeighbors get() = outerLevelNeighbors + innerLevelNeighbors + sameLevelNeighbors.filterNot { it.point.inCenter }

    private val outerLevelNeighbors get() = sequence {
        if (point.y == 0) yield(2 to 1)
        if (point.y == 4) yield(2 to 3)
        if (point.x == 0) yield(1 to 2)
        if (point.x == 4) yield(3 to 2)
    }.map { BugLocation(it, level - 1) }

    private val innerLevelNeighbors get() = (0..4).mapNotNull { i ->
        when (point) {
            2 to 1 -> i to 0
            2 to 3 -> i to 4
            1 to 2 -> 0 to i
            3 to 2 -> 4 to i
            else -> null
        }
    }.map { BugLocation(it, level + 1) }
}

private fun transformSingleLevel(current: Set<BugLocation>): Set<BugLocation> = buildSet {
    grid(0..4).forEach { point ->
        val location = BugLocation(point)
        val neighbors = location.sameLevelNeighbors.count { it in current }
        if (current.willHaveBug(location, neighbors)) add(location)
    }
}

private fun transform(current: Set<BugLocation>): Set<BugLocation> = buildSet {
    (current.minOf { it.level } - 1 .. current.maxOf { it.level } + 1).forEach { level ->
        grid(0..4).filterNot { it.inCenter }.forEach { point ->
            val location = BugLocation(point, level)
            val neighbors = location.multiLevelNeighbors.count { it in current }
            if (current.willHaveBug(location, neighbors)) add(location)
        }
    }
}

private val Point.inCenter get() = this == 2 to 2

private fun Set<BugLocation>.willHaveBug(location: BugLocation, neighbors: Int): Boolean = when (location) {
    in this -> neighbors == 1
    else -> neighbors in 1..2
}

private val Set<BugLocation>.rating: Int get() = grid(0..4).sumOf { (x, y) ->
    if (BugLocation(x to y) in this) 2.toDouble().pow(y * 5 + x).toInt() else 0
}
