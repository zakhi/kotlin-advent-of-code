package zakhi.aoc2022

import zakhi.aoc2022.Facing.*
import zakhi.helpers.*


fun main() {
    val lines = linesOf("aoc2022/day22").toList()

    val map = lines.dropLast(2).flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char != ' ') (x + 1 to y + 1) to char else null }
    }.toMap()

    val instructions = Regex("""\d+|\w""").findAll(lines.last()).map { it.value }.toList()

    val flatMapRoute = BoardMapRoute(FlatBoardMap(map))
    instructions.forEach { flatMapRoute.move(it) }
    println("The flat board password is ${flatMapRoute.password}")

    val cubeMapRoute = BoardMapRoute(CubeBoardMap(map))
    instructions.forEach { cubeMapRoute.move(it) }
    println("The cube board password is ${cubeMapRoute.password}")
}


private class BoardMapRoute(private val map: BoardMap) {
    private var position = map.start
    private var facing = RIGHT

    val password get() = 1000 * position.y + 4 * position.x + facing.ordinal

    fun move(instruction: String) {
        val steps = instruction.toIntOrNull()
        if (steps != null) repeat(steps) { advance() } else turn(instruction)
    }

    private fun advance() {
        val (newPosition, newFacing) = map.moveTo(position + facing.offset, facing)

        if (map[newPosition] == '.') {
            position = newPosition
            facing = newFacing
        }
    }

    private fun turn(direction: String) {
        facing = when (direction) {
            "R" -> facing.turnRight()
            "L" -> facing.turnLeft()
            else -> error("Invalid direction: $direction")
        }
    }
}

private sealed class BoardMap(protected val map: Map<Point, Char>) {
    val start: Point get() = map.keys.filter { it.y == 1 }.minBy { it.x }

    operator fun get(point: Point): Char = map[point] ?: fail("position not on map: $point")

    abstract fun moveTo(newPosition: Point, facing: Facing): Pair<Point, Facing>
}

private class FlatBoardMap(map: Map<Point, Char>) : BoardMap(map) {
    override fun moveTo(newPosition: Point, facing: Facing): Pair<Point, Facing> {
        if (newPosition in map) return newPosition to facing

        return when (facing) {
            RIGHT -> map.keys.filter { it.y == newPosition.y }.minBy { it.x }
            DOWN -> map.keys.filter { it.x == newPosition.x }.minBy { it.y }
            LEFT -> map.keys.filter { it.y == newPosition.y }.maxBy { it.x }
            UP -> map.keys.filter { it.x == newPosition.x }.maxBy { it.y }
        } to facing
    }
}

private class CubeBoardMap(map: Map<Point, Char>) : BoardMap(map) {
    override fun moveTo(newPosition: Point, facing: Facing): Pair<Point, Facing> {
        if (newPosition in map) return newPosition to facing

        return when (facing) {
            RIGHT -> wrapRight(newPosition)
            DOWN -> wrapDown(newPosition)
            LEFT -> wrapLeft(newPosition)
            UP -> wrapUp(newPosition)
        }
    }

    private fun wrapRight(position: Point): Pair<Point, Facing> {
        return when ((position.y - 1) / 50) {
            0 -> 100 to 150 - position.y + 1 to LEFT
            1 -> position.y + 50 to 50 to UP
            2 -> 150 to 150 - position.y + 1 to LEFT
            3 -> position.y - 100 to 150 to UP
            else -> fail("Invalid position facing right: $position")
        }
    }

    private fun wrapDown(position: Point): Pair<Point, Facing> {
        return when ((position.x - 1) / 50) {
            0 -> position.x + 100 to 1 to DOWN
            1 -> 50 to position.x + 100 to LEFT
            2 -> 100 to position.x - 50 to LEFT
            else -> fail("Invalid position facing down: $position")
        }
    }

    private fun wrapLeft(position: Point): Pair<Point, Facing> {
        return when ((position.y - 1) / 50) {
            0 -> 1 to 150 - position.y + 1 to RIGHT
            1 -> position.y - 50 to 101 to DOWN
            2 -> 51 to 150 - position.y + 1 to RIGHT
            3 -> position.y - 100 to 1 to DOWN
            else -> fail("Invalid position facing left: $position")
        }
    }

    private fun wrapUp(position: Point): Pair<Point, Facing> {
        return when ((position.x - 1) / 50) {
            0 -> 51 to position.x + 50 to RIGHT
            1 -> 1 to position.x + 100 to RIGHT
            2 -> position.x - 100 to 200 to UP
            else -> fail("Invalid position facing up: $position")
        }
    }
}

private enum class Facing(val offset: Point) {
    RIGHT(1 to 0),
    DOWN(0 to 1),
    LEFT(-1 to 0),
    UP(0 to -1);

    fun turnRight() = entries.cyclicNextFrom(ordinal)
    fun turnLeft() = entries.cyclicPreviousFrom(ordinal)
}
