package zakhi.aoc2018

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2018/day12").toList()
    val initialState = Regex("""[.#]+""").find(input.first())?.value ?: fail("Invalid initial state")

    val rules = input.drop(2).associate {
        val (source, target) = Regex("""([#.]+) => ([#.])""").matchEntire(it)?.destructured ?: fail("invalid rule: $it")
        source to target
    }

    val pots = Pots(initialState, rules)

    val plantsAfter20 = pots.potsWithPlantsAfter(20)
    println("The sum of all pots with plants is ${plantsAfter20.sum()}")

    val plantsAfter50Billion = pots.potsWithPlantsAfter(50_000_000_000)
    println("The sum of all pots with plants is ${plantsAfter50Billion.sum()}")

}


private class Pots(
    state: String,
    rules: Map<String, String>
) {
    private val initialState = state.plantIndices.toSet()

    private val newPlantRules = rules.mapNotNull { (source, target) ->
        if (target == "#") source.plantIndices.map { it - 2 }.toSet() else null
    }.toSet()


    fun potsWithPlantsAfter(generations: Long): Set<Long> {
        val (timesGrown, lastGrowStates) = (1..generations).asSequence().runningFold(initialState) { currentState, _ ->
            currentState.grow()
        }.zipWithNext().takeWhile { (previous, current) -> !current.isRightShiftOf(previous) }.withIndex().last()

        val (_, lastState) = lastGrowStates

        return when (val generation = (timesGrown + 1).toLong()) {
            generations -> lastState
            else -> lastState.map { it + generations - generation }.toSet()
        }
    }

    private fun Set<Long>.grow(): Set<Long> = (min() - 2 .. max() + 2).filter { index ->
        val surroundings = (-2L .. +2L).filter { offset -> index + offset in this }.toSet()
        surroundings in newPlantRules
    }.toSet()

    private val String.plantIndices get() = charList().mapIndexedNotNull { index, c -> if (c == '#') index.toLong() else null }

    private fun Set<Long>.isRightShiftOf(other: Set<Long>): Boolean = map { it - 1 }.toSet() == other
}
