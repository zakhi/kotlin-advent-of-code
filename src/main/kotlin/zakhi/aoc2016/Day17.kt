package zakhi.aoc2016

import zakhi.helpers.Point
import zakhi.helpers.entireTextOf
import zakhi.helpers.plus
import zakhi.helpers.x
import zakhi.helpers.y
import zakhi.helpers.md5Hash


fun main() {
    val shortestPath = findShortestPath(from = 1 to 1)
    println("The shortest path is $shortestPath")

    val longestPath = findLongestPath(from = 1 to 1)
    println("The longest path's length is ${longestPath?.length}")
}


private val passcode = entireTextOf("aoc2016/day17").trim()

private fun findShortestPath(from: Point) = findBestPath(from, "", shortest = true)
private fun findLongestPath(from: Point) = findBestPath(from, "", shortest = false)

private fun findBestPath(from: Point, currentPath: String = "", shortest: Boolean): String? {
    if (from.x !in 1..4 || from.y !in 1..4) return null
    if (from == 4 to 4) return currentPath

    val openDoors = "$passcode$currentPath".md5Hash().take(4).mapIndexedNotNull { index, code ->
        if (code in 'b'..'f') Direction.values()[index] else null
    }

    val paths = openDoors.mapNotNull { direction ->
        findBestPath(from = from + direction.offset, currentPath = currentPath + direction.name, shortest)
    }

    return if (shortest) paths.minByOrNull { it.length } else paths.maxByOrNull { it.length }
}

private enum class Direction(val offset: Point) {
    U(0 to -1),
    D(0 to 1),
    L(-1 to 0),
    R(1 to 0)
}
