package zakhi.aoc2020

import zakhi.aoc2020.FerryDirection.*
import zakhi.helpers.*


fun main() {
    val instructions = matchEachLineOf("aoc2020/day12", Regex("""([NSEWLRF])(\d+)""")) { (action, value) ->
        NavigationInstruction(action.first(), value.toInt())
    }

    val ferry = Ferry()
    ferry.move(instructions)
    println("The ferry's distance is ${ferry.distance}")

    val ferryWithWaypoint = FerryWithWaypoint()
    ferryWithWaypoint.move(instructions)
    println("The ferry with waypoint's distance is ${ferryWithWaypoint.distance}")
}


private class Ferry {
    private var position = 0 to 0
    private var direction = East

    val distance get() = position.gridDistance

    fun move(instructions: List<NavigationInstruction>) = instructions.forEach { (action, value) ->
        when (action) {
            'N' -> position += North.move(value)
            'S' -> position += South.move(value)
            'E' -> position += East.move(value)
            'W' -> position += West.move(value)
            'L' -> direction += value
            'R' -> direction += -value
            'F' -> position += direction.move(value)
        }
    }
}

private class FerryWithWaypoint {
    private var position = 0 to 0
    private var waypoint = 10 to 1

    val distance get() = position.gridDistance

    fun move(instructions: List<NavigationInstruction>) = instructions.forEach { (action, value) ->
        when (action) {
            'N' -> waypoint += North.move(value)
            'S' -> waypoint += South.move(value)
            'E' -> waypoint += East.move(value)
            'W' -> waypoint += West.move(value)
            'L' -> waypoint = waypoint.turn(value / 90)
            'R' -> waypoint = waypoint.turn((360 - value) / 90)
            'F' -> position += (waypoint * value)
        }
    }

    private fun Point.turn(amount: Int): Point = when (amount) {
        1 -> -y to x
        2 -> -x to -y
        3 -> y to -x
        else -> fail("Invalid turn amount $amount")
    }
}

private data class NavigationInstruction(
    val action: Char,
    val value: Int
)

private enum class FerryDirection(private val offset: Point) {
    East(1 to 0),
    North(0 to 1),
    West(-1 to 0),
    South(0 to -1);

    fun move(value: Int) = offset * value

    operator fun plus(angle: Int): FerryDirection {
        val turns = angle / 90
        return FerryDirection.values()[(ordinal + turns).mod(4)]
    }
}
