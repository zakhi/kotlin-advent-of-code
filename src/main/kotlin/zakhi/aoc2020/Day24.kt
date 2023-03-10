package zakhi.aoc2020

import zakhi.helpers.Point
import zakhi.helpers.linesOf
import zakhi.helpers.plus


fun main() {
    val instructions = linesOf("aoc2020/day24").toList()
    val grid = HexGrid()

    instructions.forEach { grid.flip(it) }
    println("The number of black tiles is ${grid.blackTileCount}")

    repeat(100) { grid.flipDaily() }
    println("The number of black tiles after 100 days is ${grid.blackTileCount}")
}


private class HexGrid {
    private val hexSides = mapOf(
        "e" to (2 to 0), "w" to (-2 to 0), "ne" to (1 to 1), "nw" to (-1 to 1), "se" to (1 to -1), "sw" to (-1 to -1)
    )
    private var tiles = mutableMapOf<Point, Boolean>().withDefault { false }

    val blackTileCount get() = tiles.count { it.value }

    fun flip(route: String) {
        val position = Regex("""([ns])?([ew])""").findAll(route).fold(0 to 0) { currentTile, match ->
            currentTile + hexSides.getValue(match.value)
        }

        tiles[position] = !position.flipped()
    }

    fun flipDaily() {
        val extendedTiles = tiles.keys.flatMap { neighborsOf(it) + it }.distinct().associateWith { it.flipped() }

        tiles = extendedTiles.mapValues { (tile, flipped) ->
            val flippedNeighbors = neighborsOf(tile).count { it.flipped() }

            val flippedShouldFlipAgain = flipped && (flippedNeighbors == 0 || flippedNeighbors > 2)
            val notFlippedShouldFlip = !flipped && flippedNeighbors == 2

            if (flippedShouldFlipAgain || notFlippedShouldFlip) !flipped else flipped
        }.toMutableMap().withDefault { false }
    }

    private fun Point.flipped() = tiles.getValue(this)

    private fun neighborsOf(tile: Point) = hexSides.values.map { tile + it }
}
