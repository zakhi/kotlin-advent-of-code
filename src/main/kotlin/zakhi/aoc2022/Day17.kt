package zakhi.aoc2022

import zakhi.helpers.*
import java.util.*


fun main() {
    val windSigns = entireTextOf("aoc2022/day17").trim().asSequence().map { windOffsetOf(it) }.toList()
    val tower = Tower(windSigns, rockShapes)

    repeat(2022) { tower.dropNextRock() }
    println("The height of the tower of rocks is ${tower.height}")

    val highTower = Tower(windSigns, rockShapes)
    val ids = mutableListOf(highTower.id)
    val heights = mutableMapOf(highTower.id to 0)

    while (true) {
        highTower.dropNextRock()
        if (highTower.id in heights) break

        heights[highTower.id] = highTower.height
        ids.add(highTower.id)
    }

    val baseIndex = ids.indexOf(highTower.id)
    val iterations = 1000000000000L

    val preRepeatingHeight = heights.getValue(highTower.id)
    val repeatingHeight = (iterations - ids.size) / (ids.size - baseIndex) * (highTower.height - preRepeatingHeight)

    val remainderId = ids[baseIndex + ((iterations - ids.size) % (ids.size - baseIndex)).toInt()]
    val remainderHeight = heights.getValue(remainderId) - preRepeatingHeight

    println("The height of the high tower of rocks is ${highTower.height + repeatingHeight + remainderHeight}")
}


private class Tower(
    private val winds: List<Int>,
    private val rocks: List<RockShape>
) {
    private var occupiedPoints = mutableSetOf<Point>()

    private var windIndex = 0
    private var rockIndex = 0

    val height: Int get() = occupiedPoints.maxOfOrNull { it.y } ?: 0
    val id: Int get() = Objects.hash(topLevels, windIndex, rockIndex)

    fun dropNextRock() {
        val rock = nextRockShape.at(3 to height + 4)

        while (true) {
            rock.moveIfPossible(nextWindOffset)

            if (!rock.moveIfPossible(0 to -1)) {
                stop(rock)
                break
            }
        }
    }

    private val nextRockShape get() = rocks[rockIndex].also { rockIndex = (rockIndex + 1).mod(rocks.size) }
    private val nextWindOffset get() = (winds[windIndex] to 0).also { windIndex = (windIndex + 1).mod(winds.size) }

    private fun Rock.moveIfPossible(offset: Point): Boolean {
        if (pointsWithOffset(offset).all { it.free() }) {
            move(offset)
            return true
        }

        return false
    }

    private fun stop(rock: Rock) {
        occupiedPoints.addAll(rock.points)
    }

    private fun Point.free() = x in 1..7 && y > 0 && this !in occupiedPoints

    private val topLevels: List<Point> get() {
        val minTopY = height - 20
        return occupiedPoints.filter { it.y >= minTopY }.map { it.x to it.y - minTopY }
    }
}

private class RockShape(val points: List<Point>) {
    fun at(position: Point) = Rock(this, position)
}

private class Rock(
    private val shape: RockShape,
    private var position: Point
) {
    val points get() = shape.points.map { it + position }

    fun pointsWithOffset(offset: Point) = points.map { it + offset }

    fun move(offset: Point) {
        position += offset
    }
}

private val rockShapes = listOf(
    RockShape(listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)),
    RockShape(listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2)),
    RockShape(listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2)),
    RockShape(listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3)),
    RockShape(listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1))
)

private fun windOffsetOf(char: Char): Int = when (char) {
    '>' -> 1
    '<' -> -1
    else -> fail("Invalid wind character: $char")
}
