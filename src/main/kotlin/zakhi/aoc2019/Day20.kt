package zakhi.aoc2019

import zakhi.aoc2019.Ring.*
import zakhi.helpers.*


fun main() {
    val simpleMaze = PlutoMaze()
    val distance = minimalDistance(simpleMaze.start, simpleMaze::isEnd, simpleMaze::neighborsOf)
    println("The number of steps required is $distance")

    val multiLevelMaze = PlutoMaze(multiLevel = true)
    val distanceForMultiLevel = minimalDistance(multiLevelMaze.start, multiLevelMaze::isEnd, multiLevelMaze::neighborsOf)
    println("The number of steps required in the multi level mze is $distanceForMultiLevel")
}


private val input = linesOf("aoc2019/day20").flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, char ->
        if (char == '.' || char.isUpperCase()) (x to y) to char else null
    }
}.toMap()

private data class Tile(
    val point: Point,
    val level: Int = 1
)

private enum class Ring(val increment: Int) {
    Inner(1), Outer(-1);

    fun other() = values().first { it != this }
}

private data class Portal(
    val id: Set<Char>,
    val ring: Ring
) {
    companion object {
        val AA = Portal(setOf('A'), Outer)
        val ZZ = Portal(setOf('Z'), Outer)
    }

    fun matchingPortal() = copy(ring = ring.other())
}

private class PlutoMaze(
    private val multiLevel: Boolean = false
) {
    val start: Tile get() = Tile(portalTilePoints.getValue(Portal.AA))

    fun isEnd(tile: Tile): Boolean = tile == Tile(portalTilePoints.getValue(Portal.ZZ))

    fun neighborsOf(tile: Tile) = sequence {
        yieldAll(tile.point.adjacentNeighbors.filter { it.isTile() }.map { tile.copy(point = it) })

        portals[tile.point]?.let { portal ->
            val otherPoint = portalTilePoints[portal.matchingPortal()]
            val otherLevel = if (multiLevel) tile.level + portal.ring.increment else 1

            if (otherPoint != null && otherLevel > 0) {
                yield(Tile(otherPoint, otherLevel))
            }
        }
    }.map { it to 1 }.toList()

    private val portalTilePoints = buildMap {
        val portalLetters = input.filter { it.value.isUpperCase() }

        portalLetters.forEach { (point, char) ->
            point.adjacentNeighbors.find { it.isTile() }?.let { tilePoint ->
                val id = setOf(char, point.adjacentNeighbors.firstNotNullOf { portalLetters[it] })
                val portal = Portal(id, point.ring)
                put(portal, tilePoint)
            }
        }
    }

    private val portals = portalTilePoints.entries.associate { it.value to it.key }

    private fun Point.isTile(): Boolean = input[this] == '.'

    private val Point.ring: Ring get() = when {
        x == 1 || y == 1 -> Outer
        x == input.keys.maxOf { it.x } - 1 -> Outer
        y == input.keys.maxOf { it.y } - 1 -> Outer
        else -> Inner
    }
}
