package zakhi.aoc2020

import zakhi.aoc2020.Direction.*
import zakhi.helpers.*


fun main() {
    val tiles = entireTextOf("aoc2020/day20").trim().split("\n\n").map { tileFrom(it.lines()) }
    val imageConstruction = ImageConstruction(tiles)
    val cornerTilesProduct = imageConstruction.corners().map { it.id.toLong() }.product()

    println("The product of the corner tile IDs is $cornerTilesProduct")

    val image = imageConstruction.assemble()

    val safeWaters = image.count - image.countOccurrencesOf(seaMonster) * seaMonster.count
    println("The number of # not part of a sea monster is $safeWaters")
}


private val seaMonster = """
                      # 
    #    ##    ##    ###
     #  #  #  #  #  #     
    """.trimIndent().let { Image(it.lines()) }

private class Tile(val id: Int, private val rows: List<String>) {
    private val allEdges = run {
        val noFlippedEdges = listOf(rows.first(), rows.join { it.last().toString() }, rows.last(), rows.join { it.first().toString() })
        noFlippedEdges + noFlippedEdges.map { it.reversed() }
    }

    private var transformations = 0

    fun sharesEdgesWith(other: Tile, direction: Direction? = null): Boolean {
        if (other == this) return false
        val sharedEdges = allEdges.intersect(other.allEdges.toSet())

        if (sharedEdges.isEmpty()) return false
        return direction == null || edgeAt(direction) in sharedEdges
    }

    fun transformTo(edge: String, direction: Direction) {
        while (edgeAt(direction) != edge) transform()
    }

    fun transform() {
        transformations = (transformations + 1).mod(edgeStates.size)
    }

    fun edgeAt(direction: Direction): String {
        val edgeIndices = edgeStates[transformations]
        return allEdges[edgeIndices[direction.ordinal]]
    }

    fun withoutBorders(): List<String> = transformedRows.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }

    private val transformedRows get() = if (transformations == 0) rows else rows.allTransformations().take(transformations).last()

    companion object {
        private val edgeStates = listOf(
            listOf(0, 1, 2, 3), listOf(4, 3, 6, 1),
            listOf(1, 6, 3, 4), listOf(5, 4, 7, 6),
            listOf(6, 7, 4, 5), listOf(2, 5, 0, 7),
            listOf(7, 0, 5, 2), listOf(3, 2, 1, 0)
        )
    }
}

private class ImageConstruction(tiles: List<Tile>) {
    private val neighbors = tiles.associateWith { tile -> tiles.filter { tile.sharesEdgesWith(it) } }
    private val dimension = tiles.size.floorSqrt()
    private val assembledImage = mutableMapOf<Point, Tile>()
    private val placedTiles = mutableSetOf<Tile>()

    fun corners() = neighbors.filterValues { it.size == 2 }.keys

    fun assemble(): Image {
        val firstCorner = corners().first()
        val (neighbor1, neighbor2) = neighbors.getValue(firstCorner)

        while (!firstCorner.sharesEdgesWith(neighbor1, direction = DOWN) || !firstCorner.sharesEdgesWith(neighbor2, direction = RIGHT)) {
            firstCorner.transform()
        }

        placeTile(firstCorner, position = 0 to 0)

        val assemblesRows = (0..<dimension).map { y ->
            (0..<dimension).map { x -> assembledImage.getValue(x to y).withoutBorders() }
        }.flatMap { it.transpose() }.map { it.join() }

        return Image(assemblesRows)
    }

    private fun placeTile(tile: Tile, position: Point) {
        assembledImage[position] = tile
        placedTiles.add(tile)

        neighbors.getValue(tile).forEach { neighbor ->
            if (neighbor !in placedTiles) {
                if (tile.sharesEdgesWith(neighbor, direction = RIGHT)) {
                    neighbor.transformTo(tile.edgeAt(RIGHT), LEFT)
                    placeTile(neighbor, position + (1 to 0))
                } else if (tile.sharesEdgesWith(neighbor, direction = DOWN)) {
                    neighbor.transformTo(tile.edgeAt(DOWN), UP)
                    placeTile(neighbor, position + (0 to 1))
                }
            }
        }
    }
}

private class Image(private val rows: List<String>) {
    private val points = rows.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') x to y else null }
    }

    val count: Int = points.size

    fun countOccurrencesOf(other: Image): Int = other.rows.allTransformations().map { transformedRows ->
        val transformedOther = Image(transformedRows)

        grid(0..width - transformedOther.width, 0..height - transformedOther.height).count { start ->
            transformedOther.points.all { it + start in points }
        }
    }.first { it > 0 }

    private val height get() = rows.size
    private val width get() = rows.maxOf { it.length }
}

private enum class Direction {
    UP, RIGHT, DOWN, LEFT
}

private fun tileFrom(lines: List<String>): Tile {
    val id = lines.first().replace(Regex("""\D"""), "").toInt()
    return Tile(id, lines.drop(1))
}

private fun List<String>.allTransformations(): Sequence<List<String>> {
    var current = map { it.toList() }
    return sequence {
        repeat(8) { i ->
            current = if (i.isEven) current.map { it.reversed() } else current.transpose()
            yield(current.map { it.join() })
        }
    }
}
