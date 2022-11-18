package zakhi.aoc2017

import zakhi.helpers.*


fun main() {
    val instructions = entireTextOf("aoc2017/day11").trim().split(",")
    val track = HexGridTrack()
    instructions.forEach { track.go(it) }

    println("The number of steps from the start is ${track.distanceFromStart}")
    println("The furthest distance is ${track.furthestDistance}")
}

class HexGridTrack {
    private var position = 0 to 0

    var furthestDistance = 0
        private set

    val distanceFromStart: Int get() = maxOf(position.x, position.y / 2)

    fun go(direction: String) {
        position += when (direction) {
            "n" -> 0 to 2
            "s" -> 0 to -2
            "ne" -> 1 to 1
            "se" -> 1 to -1
            "nw" -> -1 to 1
            "sw" -> -1 to -1
            else -> fail("Invalid direction $direction")
        }

        furthestDistance = maxOf(furthestDistance, distanceFromStart)
    }
}
