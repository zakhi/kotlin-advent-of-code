package zakhi.aoc2016

import zakhi.aoc2015.Point
import zakhi.aoc2015.gridDistance
import zakhi.aoc2015.plus
import zakhi.cyclicNext
import zakhi.cyclicPrevious
import zakhi.findAllInEntireTextOf


fun main() {
    val instructions = findAllInEntireTextOf("aoc2016/day1", Regex("""([RL])(\d+)""")) { (turn, blocks) -> turn to blocks.toInt() }

    val route = Route()
    instructions.forEach { (turn, blocks) -> route.move(turn, blocks) }

    println("The HQ is ${route.destination.gridDistance} block away")
    println("The first location visited twice is ${route.firstRevisited.gridDistance} block away")
}


private class Route {
    private val positions = mutableListOf(0 to 0)
    private var direction = directions.first()

    lateinit var firstRevisited: Point
        private set

    val destination: Point get() = positions.last()

    fun move(turn: String, blocks: Int) {
        direction = when (turn) {
            "R" -> directions.cyclicNext(directions.indexOf(direction))
            "L" -> directions.cyclicPrevious(directions.indexOf(direction))
            else -> throw Exception("Invalid turn $turn")
        }

        repeat(blocks) {
            val newPosition = positions.last() + direction
            if (!::firstRevisited.isInitialized && newPosition in positions) firstRevisited = newPosition

            positions.add(newPosition)
        }
    }
}

private val directions: List<Point> = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)