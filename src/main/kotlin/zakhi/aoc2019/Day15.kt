package zakhi.aoc2019

import zakhi.aoc2019.DroidDirection.North
import zakhi.aoc2019.DroidStatus.*
import zakhi.aoc2019.LocationType.*
import zakhi.helpers.*
import java.util.LinkedList


fun main() {
    val program = readProgramFrom(entireTextOf("aoc2019/day15"))
    val droidArea = DroidArea(program)
    droidArea.findOxygenSystem()

    println("The fewest move commands to oxygen system is ${droidArea.distanceToOxygenSystem}")
    println("The number of minutes to fill the room with oxygen is ${droidArea.maxDistanceForOxygen}")
}

private class DroidArea(program: List<Int>) {
    private val droid = IntCodeComputer(program, ::provideInput, ::handleOutput)
    private val route = LinkedList(listOf(0 to 0))
    private val roomMap = mutableMapOf(route.last to Empty)
    private val distanceMap = mutableMapOf(route.last to 0).withDefault { Int.MAX_VALUE }

    fun findOxygenSystem() {
        droid.start()
    }

    val distanceToOxygenSystem: Int get() = distanceMap.getValue(oxygenSystemPosition)

    val maxDistanceForOxygen: Int get() {
        var queue = listOf(oxygenSystemPosition)
        val visited = mutableSetOf<Point>()
        var distance = 0

        while (queue.isNotEmpty()) {
            val nextPositions = queue.flatMap { it.adjacentNeighbors }.filter { it in roomMap && roomMap[it] != Wall && it !in visited }
            if (nextPositions.isNotEmpty()) distance += 1

            visited.addAll(nextPositions)
            queue = nextPositions
        }

        return distance
    }

    private fun provideInput(): Int {
        val position = route.last
        val nextUnknown = position.adjacentNeighbors.find { it !in roomMap }

        if (nextUnknown != null) {
            route.add(nextUnknown)
            return DroidDirection.findByOffset(nextUnknown - position).value
        }

        if (route.size == 1) {
            droid.stop()
            return North.value
        }

        route.removeLast()
        return DroidDirection.findByOffset(route.last - position).value
    }

    private fun handleOutput(value: Int) {
        val status = DroidStatus.of(value)
        val position = route.last

        roomMap[position] = when (status) {
            Moved -> Empty
            MovedAndFoundOxygenSystem -> OxygenSystem
            HitWall -> Wall
        }

        if (status != HitWall) {
            distanceMap[position] = minOf(distanceMap.getValue(position), route.size - 1)
        } else {
            route.removeLast()
        }
    }

    private val oxygenSystemPosition get() = roomMap.entries.first { (_, type) -> type == OxygenSystem }.key
}

private enum class LocationType {
    Wall,
    Empty,
    OxygenSystem
}

private enum class DroidStatus {
    HitWall,
    Moved,
    MovedAndFoundOxygenSystem;

    companion object {
        fun of(value: Int) = values().first { it.ordinal == value }
    }
}

private enum class DroidDirection(
    val value: Int,
    val offset: Point
) {
    North(1, 0 to 1),
    South(2, 0 to -1),
    West(3, -1 to 0),
    East(4, 1 to 0);

    companion object {
        fun findByOffset(offset: Point) = DroidDirection.values().first { it.offset == offset }
    }
}
