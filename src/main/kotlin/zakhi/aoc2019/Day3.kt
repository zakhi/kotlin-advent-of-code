package zakhi.aoc2019

import zakhi.helpers.*


fun main() {
    val (wire1, wire2) = linesOf("aoc2019/day3").map { createWire(it.trim().split(",")) }.toList()
    val intersections = wire1 intersect wire2.toSet()

    val distanceToClosestIntersection = intersections.minOf { it.gridDistance }
    println("The distance to the closest intersection is $distanceToClosestIntersection")

    val fewestStepsToIntersection = intersections.minOf { point -> wire1.indexOf(point) + wire2.indexOf(point) } + 2
    println("The fewest combined steps to an intersection is $fewestStepsToIntersection")
}


private fun createWire(instructions: List<String>): List<Point> = buildList {
    var position = 0 to 0

    instructions.forEach { instruction ->
        val (direction, steps) = Regex("""(\w)(\d+)""").matchEntire(instruction)?.destructured ?: fail("Invalid instruction $instruction")

        repeat(steps.toInt()) {
            position += offsets.getValue(direction)
            add(position)
        }
    }
}

private val offsets = mapOf(
    "R" to (1 to 0),
    "L" to (-1 to 0),
    "U" to (0 to 1),
    "D" to (0 to -1)
)
