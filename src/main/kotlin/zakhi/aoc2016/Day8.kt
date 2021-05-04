package zakhi.aoc2016

import zakhi.Point
import zakhi.input.linesOf
import zakhi.points.grid
import zakhi.points.x
import zakhi.points.y


fun main() {
    val instructions = linesOf("aoc2016/day8").toList()
    val display = LightDisplay()
    instructions.forEach { display.perform(it) }

    println("The number of lit lights are ${display.numberOfLights}")

    println("The display shows:")
    display.print()
}


private class LightDisplay {

    private var lights = setOf<Point>()

    val numberOfLights get() = lights.size

    fun perform(operation: String) {
        lightRectangle(operation) || rotateRow(operation) || rotateColumn(operation) || throw Exception("invalid operation $operation")
    }

    fun print() {
        repeat(screenHeight) { y ->
            repeat(screenWidth) { x->
                print(if (x to y in lights) '#' else ' ')
                if (x % 5 == 4) print("  ")
            }

            println()
        }
    }

    private fun lightRectangle(operation: String): Boolean {
        val match = Regex("""rect (\d+)x(\d+)""").matchEntire(operation) ?: return false
        val (columns, rows) = match.groupValues.drop(1).map { it.toInt() }

        lights = lights + grid(0 until columns, 0 until rows).toList()
        return true
    }

    private fun rotateRow(operation: String): Boolean {
        val match = Regex("""rotate row y=(\d+) by (\d+)""").matchEntire(operation) ?: return false
        val (row, times) = match.groupValues.drop(1).map { it.toInt() }

        lights = lights.map { point -> if (point.y == row) point.rotateRight(times) else point }.toSet()
        return true
    }

        private fun rotateColumn(operation: String): Boolean {
        val match = Regex("""rotate column x=(\d+) by (\d+)""").matchEntire(operation) ?: return false
        val (column, times) = match.groupValues.drop(1).map { it.toInt() }

        lights = lights.map { point -> if (point.x == column) point.rotateDown(times) else point }.toSet()
        return true
    }
}

private const val screenWidth = 50
private const val screenHeight = 6

private fun Point.rotateRight(times: Int): Point {
    val newX = (x + times) % screenWidth
    return newX to y
}

private fun Point.rotateDown(times: Int): Point {
    val newY = (y + times) % screenHeight
    return x to newY
}
