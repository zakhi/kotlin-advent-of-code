package zakhi.aoc2017

import zakhi.aoc2017.CarrierDirection.*
import zakhi.aoc2017.NodeState.*
import zakhi.helpers.*


fun main() {
    val infected = linesOf("aoc2017/day22").flatMapIndexed { row, line ->
        line.withIndex().mapNotNull { (column, char) ->
            if (char == '#') column to row else null
        }
    }.toSet()

    val location = infected.maxOf { (row, _) -> row } / 2 to infected.maxOf { (_, col) -> col } / 2
    val carrier = VirusCarrier(infected, location)
    repeat(10_000) { carrier.burst() }

    println("The number of bursts infecting a node is ${carrier.infectingBursts}")

    val evolvedCarrier = VirusCarrier(infected, location, evolved = true)
    repeat(10_000_000) { evolvedCarrier.burst() }

    println("The number of evolved bursts infecting a node is ${evolvedCarrier.infectingBursts}")
}


private class VirusCarrier(
    initialInfected: Set<Point>,
    initialLocation: Point,
    private var evolved: Boolean = false
) {
    private var nodes = initialInfected.associateWith { Infected }.toMutableMap().withDefault { Clean }
    private var location = initialLocation
    private var direction = Up

    var infectingBursts = 0
        private set

    fun burst() {
        when (nodes.getValue(location)) {
            Clean -> {
                direction = direction.turnLeft()
                nodes[location] = if (evolved) Weakened else Infected
            }
            Weakened -> {
                nodes[location] = Infected
            }
            Infected -> {
                direction = direction.turnRight()
                nodes[location] = if (evolved) Flagged else Clean
            }
            Flagged -> {
                direction = direction.turnRight().turnRight()
                nodes[location] = Clean
            }
        }

        if (nodes.getValue(location) == Infected) infectingBursts += 1
        location += direction.offset
    }
}

private enum class CarrierDirection(val offset: Point) {
    Up(0 to -1),
    Right(1 to 0),
    Down(0 to 1),
    Left(-1 to 0);

    fun turnRight(): CarrierDirection = values().toList().cyclicNextFrom(ordinal)
    fun turnLeft(): CarrierDirection = values().toList().cyclicPreviousFrom(ordinal)
}

private enum class NodeState {
    Clean, Weakened, Infected, Flagged
}
