package zakhi.aoc2022

import zakhi.helpers.combinations
import zakhi.helpers.matchEachLineOf


fun main() {
    val valves = matchEachLineOf("aoc2022/day16", Regex("""Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? ([\w, ]+)""")) { (name, rate, tunnels) ->
        Valve(name, rate.toInt(), tunnels.split(", "))
    }

    val distances = findDistances(valves)

    val mostPressure = findMostPressure(valves, distances)
    println("The most pressure that can be released is $mostPressure")

    val mostPressureWithElephant = findMostPressureWithElephant(valves, distances)
    println("The most pressure that can be released with the elephant is $mostPressureWithElephant")
}


private fun findDistances(valves: List<Valve>): Map<Pair<Valve, Valve>, Int> {
    val distances = mutableMapOf<Pair<Valve, Valve>, Int>().withDefault { 1000 }

    for (valve in valves) {
        distances[valve to valve] = 0

        valve.tunnelsTo.forEach { other ->
            distances[valve to valves.first { it.name == other }] = 1
        }
    }

    for (via in valves) {
        for (from in valves) {
            for (to in valves) {
                distances[from to to] = minOf(distances.getValue(from to to), distances.getValue(from to via) + distances.getValue(via to to))
            }
        }
    }

    return distances.filterKeys { (from, to) -> (from.flowRate > 0  || from.name == "AA") && to.flowRate > 0 }
}

private fun findMostPressure(valves: List<Valve>, distances: Map<Pair<Valve, Valve>, Int>, time: Int = 30): Int {
    val initialState = ValveState(valves.first { it.name == "AA" }, time, valves.filter { it.flowRate > 0 }.toSet())

    val queue = mutableListOf(initialState)
    val visited = mutableSetOf<ValveState>()
    var bestState = initialState

    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        visited.add(state)

        if (state.totalPressure > bestState.totalPressure) {
            bestState = state
        }

        if (state.potentialPressure < bestState.potentialPressure) {
            continue
        }

        state.nextStates(distances).filterNot { it in visited }.forEach { queue.add(it) }
    }

    return bestState.totalPressure
}

private fun findMostPressureWithElephant(valves: List<Valve>, distances: Map<Pair<Valve, Valve>, Int>): Int {
    val valvesToOpen = valves.filter { it.flowRate > 0 }

    return (1..valvesToOpen.size / 2).asSequence().flatMap { splitCount -> valvesToOpen.combinations(splitCount) }.maxOf { manValvesToOpen ->
        val elephantValvesToOpen = valvesToOpen - manValvesToOpen.toSet()
        findMostPressure(valves - elephantValvesToOpen.toSet(), distances, 26) + findMostPressure(valves - manValvesToOpen.toSet(), distances, 26)
    }
}

private data class Valve(
    val name: String,
    val flowRate: Int,
    val tunnelsTo: List<String>,
) {
    override fun toString(): String = name
}

private data class ValveState(
    val current: Valve,
    val timeLeft: Int,
    val valvesLeftToOpen: Set<Valve>,
    val totalPressure: Int = 0,
) {
    val potentialPressure: Int get() = totalPressure + valvesLeftToOpen.sumOf { it.flowRate } * timeLeft

    fun nextStates(distances: Map<Pair<Valve, Valve>, Int>): List<ValveState> {
        if (timeLeft == 0) return emptyList()

        return distances.filterKeys { (from, _) -> from == current }.mapKeys { (valves, _) -> valves.second }
            .filter { (nextValve, distance) -> nextValve in valvesLeftToOpen && distance < timeLeft }
            .map { (nextValve, distance) ->
                val newTimeLeft = timeLeft - distance - 1
                ValveState(
                    current = nextValve,
                    timeLeft = newTimeLeft,
                    valvesLeftToOpen = valvesLeftToOpen - nextValve,
                    totalPressure = totalPressure + nextValve.flowRate * newTimeLeft,
                )
            }
    }
}
