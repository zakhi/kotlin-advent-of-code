package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val instructions = matchEachLineOf("aoc2022/day9", Regex("""(\w) (\d+)""")) { (direction, times) ->
        directionOffset(direction) to times.toInt()
    }

    val shortRope = Rope(length = 2)
    instructions.forEach { (direction, times) -> shortRope.follow(direction, times) }
    println("The number of positions the short rope tail visited is ${shortRope.tailVisitedLocations}")

    val longRope = Rope(length = 10)
    instructions.forEach { (direction, times) -> longRope.follow(direction, times) }
    println("The number of positions the long rope tail visited is ${longRope.tailVisitedLocations}")
}


private class Rope(val length: Int) {
    private val knots = (0..<length).map { 0 to 0 }.toMutableList()
    private val tailVisited = mutableSetOf(0 to 0)

    val tailVisitedLocations get() = tailVisited.size

    fun follow(direction: Point, times: Int) {
        repeat(times) {
            knots[0] += direction

            (1..<length).forEach { index ->
                val (previous, current) = knots.slice(index - 1..index)

                if (previous != current && previous !in current.allNeighbors) {
                    val moveCandidates = if (previous.y == current.y || previous.x == current.x) current.adjacentNeighbors else current.diagonalNeighbors
                    knots[index] = moveCandidates.first { it in previous.allNeighbors }
                }
            }

            tailVisited.add(knots.last())
        }
    }
}

private fun directionOffset(direction: String) = when (direction) {
    "U" -> 0 to 1
    "D" -> 0 to -1
    "R" -> 1 to 0
    "L" -> -1 to 0
    else -> fail("Invalid direction: $direction")
}
