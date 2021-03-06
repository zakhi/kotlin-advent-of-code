package zakhi.aoc2015

import zakhi.aoc2015.Operation.*
import zakhi.helpers.Point
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.grid
import zakhi.helpers.toPoint
import zakhi.helpers.x
import zakhi.helpers.y


fun main() {
    val instructions = matchEachLineOf("aoc2015/day6", Regex("""(toggle|turn on|turn off) ([\d,]+) through ([\d,]+)""")) { (operation, topLeft, bottomRight) ->
        Instruction(Operation.valueOf(operation), topLeft.toPoint(), bottomRight.toPoint())
    }

    println("The number of lights lit is ${numberOfLitLights(instructions)}")
    println("Total brightness is ${totalBrightness(instructions)}")
}


private enum class Operation {
    `turn on`, `turn off`, toggle
}

private class Instruction(
    val operation: Operation,
    private val topLeft: Point,
    private val bottomRight: Point
) {
    val points get() = grid(topLeft.x..bottomRight.x, topLeft.y..bottomRight.y)
}

private fun numberOfLitLights(instructions: List<Instruction>): Int {
    val grid = mutableMapOf<Point, Boolean>().withDefault { false }

    instructions.forEach {
        it.points.forEach { point ->
            grid[point] = when (it.operation) {
                `turn on` -> true
                `turn off` -> false
                toggle -> !grid.getValue(point)
            }
        }
    }

    return grid.values.count { it }
}

private fun totalBrightness(instructions: List<Instruction>): Int {
    val grid = mutableMapOf<Point, Int>().withDefault { 0 }

    instructions.forEach {
        it.points.forEach { point ->
            val change = when (it.operation) {
                `turn on` -> 1
                `turn off` -> -1
                toggle -> 2
            }

            grid[point] = maxOf(0, grid.getValue(point) + change)
        }
    }

    return grid.values.sum()
}

private fun String.toPoint() = split(",").map { it.toInt() }.toPoint()
