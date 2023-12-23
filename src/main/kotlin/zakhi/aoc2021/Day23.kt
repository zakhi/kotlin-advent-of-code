package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2021/day23").toList()
    val burrow = parseBurrow(input)

    val minimalEnergy = minimalEnergyForOrganization(burrow)
    println("The minimal energy required to organize the burrow is $minimalEnergy")

    val unfoldedBurrow = input.toMutableList().apply {
        addAll(3, listOf("  #D#C#B#A#", "  #D#B#A#C#"))
    }.let { parseBurrow(it) }

    val unfoldedMinimalEnergy = minimalEnergyForOrganization(unfoldedBurrow)
    println("The minimal energy required to organize the unfolded burrow is $unfoldedMinimalEnergy")
}

private fun parseBurrow(input: List<String>): Burrow {
    return input.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char != '#') (x to y) to char else null }
    }.let { Burrow(it.toMap()) }
}

private data class Amphipod(
    val type: Char,
    val roomColumn: Int,
    val stepEnergy: Int
)

private val amphipods = listOf(
    Amphipod('A', 3, 1),
    Amphipod('B', 5, 10),
    Amphipod('C', 7, 100),
    Amphipod('D', 9, 1000),
).associateBy { it.type }

private val entrances = amphipods.map { it.value.roomColumn to 1 }.toSet()

private data class Burrow(val map: Map<Point, Char>) {
    val organized: Boolean get() = amphipods.values.all { roomFilled(it) }

    val moves: List<Pair<Burrow, Int>> get() =
        map.entries.filter { it.value in amphipods.keys }.flatMap { (position, type) ->
            val amphipod = amphipods.getValue(type)

            destinationsFor(amphipod, position).map { (destination, steps) ->
                val newMap = map.toMutableMap().apply {
                    set(position, '.')
                    set(destination, type)
                }

                Burrow(newMap) to amphipod.stepEnergy * steps
            }
        }

    private fun destinationsFor(amphipod: Amphipod, position: Point): List<Pair<Point, Int>> {
        if (!allowedToMove(amphipod, position)) return emptyList()

        return allDestinations(position).filter { (destination, _) ->
            when {
                destination.inHallway() -> !position.inHallway() && destination !in entrances
                else -> validRoomDestination(destination, amphipod)
            }
        }
    }

    private fun allowedToMove(amphipod: Amphipod, position: Point): Boolean =
        (position.x != amphipod.roomColumn) || roomPositionsUnder(position, amphipod).any { it.value != amphipod.type }

    private fun allDestinations(position: Point): List<Pair<Point, Int>> {
        val visited = mutableMapOf(position to 0)
        val next = mutableListOf(position to 0)

        while (next.isNotEmpty()) {
            val (current, steps) = next.removeFirst()
            val nextSteps = steps + 1

            current.adjacentNeighbors.filter { it !in visited && map[it] == '.' }.forEach {
                visited[it] = nextSteps
                next.add(it to nextSteps)
            }
        }

        return visited.filter { it.value > 0 }.toList()
    }

    private fun validRoomDestination(destination: Point, amphipod: Amphipod) =
        destination.x == amphipod.roomColumn && roomPositionsUnder(destination, amphipod).all { it.value == amphipod.type }

    private fun roomFilled(amphipod: Amphipod) = roomPositions(amphipod).all { it.value == amphipod.type }

    private fun roomPositions(amphipod: Amphipod) = map.filterKeys { position ->
        position.x == amphipod.roomColumn && !position.inHallway()
    }

    private fun roomPositionsUnder(position: Point, amphipod: Amphipod) = roomPositions(amphipod).filterKeys { it.y > position.y }

    private fun Point.inHallway() = y == 1
}

private fun minimalEnergyForOrganization(burrow: Burrow): Int {
    val queue = minHeapOf(burrow to 0)
    var minimum = Int.MAX_VALUE

    while (!queue.isEmpty()) {
        val (current, energy) = queue.remove()
        if (energy > minimum) continue

        if (current.organized) {
            minimum = minOf(minimum, energy)
        } else {
            current.moves.forEach { (next, energyToNext) ->
                queue[next] = minOf(queue[next], energy + energyToNext)
            }
        }
    }

    return minimum
}
