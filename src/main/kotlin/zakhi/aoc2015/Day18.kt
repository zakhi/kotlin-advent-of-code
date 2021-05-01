package zakhi.aoc2015

import zakhi.*


fun main() {
    val initialState = linesOf("aoc2015/day18").flatMapIndexed { y, line ->
        line.mapIndexed { x, symbol -> (x to y) to (symbol == '#') }
    }.toMap().withDefault { false }

    val finalState = (1..100).fold(initialState) { state, _ -> transformLights(state) }
    println("The number of lights on after 100 steps is ${finalState.count { it.value }}")

    val finalStateWithStuckLights = (1..100).fold(initialState.withStuckLights()) { state, _ -> transformLights(state).withStuckLights() }
    println("The number of lights on after 100 steps with lights stuck is ${finalStateWithStuckLights.count { it.value }}")
}


private fun transformLights(state: Map<Pair<Int, Int>, Boolean>) = grid(0 until 100).associateWith { transform(it, state) }.withDefault { false }

private fun transform(point: Point, state: Map<Point, Boolean>): Boolean {
    val neighborLightsOn = neighborOffsets.count { (dx, dy) -> state.getValue((point.x + dx) to (point.y + dy)) }

    return when (state.getValue(point)) {
        true -> neighborLightsOn in listOf(2, 3)
        false -> neighborLightsOn == 3
    }
}

private fun Map<Point, Boolean>.withStuckLights(): Map<Point, Boolean> = (this + grid(listOf(0, 99)).map { it to true }).withDefault { false }

private val neighborOffsets = grid(-1..1).filterNot { it == 0 to 0 }
