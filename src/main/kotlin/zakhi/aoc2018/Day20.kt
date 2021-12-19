package zakhi.aoc2018

import zakhi.helpers.Point
import zakhi.helpers.entireTextOf
import zakhi.helpers.max
import zakhi.helpers.plus


fun main() {
    val directions = entireTextOf("aoc2018/day20").trim().toCharArray()
    trace(directions.toMutableList())

    println("The largest number of doors to reach a room is $furthestRoomDistance")
    println("The number of rooms with distance of at least 1000 doors is $distantRooms")
}

private val distances = mutableMapOf((0 to 0) to 0).withDefault { Int.MAX_VALUE }

private val furthestRoomDistance get() = distances.values.max()
private val distantRooms get() = distances.count { it.value >= 1000 }

private fun trace(directions: MutableList<Char>) {
    val positions = mutableSetOf(0 to 0)

    while (true) {
        when (val char = directions.removeFirst()) {
            '^' -> continue
            '$' -> break
            'N', 'W', 'S', 'E' -> step(char, positions)
            '(' -> positions.addAll(traceSplit(directions, positions))
        }
    }
}

private fun traceSplit(directions: MutableList<Char>, fromPositions: MutableSet<Point>): Set<Point> {
    val startPositions = fromPositions.toSet()
    val newPositions = mutableSetOf<Point>()
    var currentPositions = fromPositions

    while (true) {
        when (val char = directions.removeFirst()) {
            'N', 'W', 'S', 'E' -> step(char, currentPositions)
            '(' -> newPositions.addAll(traceSplit(directions, currentPositions))
            ')' -> break
            '|' -> {
                currentPositions = startPositions.toMutableSet()
                newPositions.addAll(currentPositions)
            }
        }
    }

    return newPositions
}

private fun step(direction: Char, positions: MutableSet<Point>) {
    val offset = offsetOf(direction)

    positions.replace { position ->
        (position + offset).also { newPosition ->
            if (newPosition !in distances) distances[newPosition] = distances.getValue(position) + 1
        }
    }
}

private fun offsetOf(direction: Char) = when (direction) {
    'N' -> 0 to 1
    'S' -> 0 to -1
    'W' -> 1 to 0
    'E' -> -1 to 0
    else -> throw Exception("Invalid direction $direction")
}

private fun <E> MutableSet<E>.replace(replacement: (E) -> E) {
    val newValues = map(replacement)
    clear()
    addAll(newValues)
}
