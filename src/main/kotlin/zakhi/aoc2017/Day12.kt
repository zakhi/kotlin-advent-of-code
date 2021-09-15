package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf


fun main() {
    val pipes = matchEachLineOf("aoc2017/day12", Regex("""(\d+) <-> ([\d, ]+)""")) { (pipe, connections) ->
        pipe.toInt() to connections.split(", ").map { it.toInt() }.toSet()
    }.toMap()

    val zeroPool = getPoolFor(0, pipes)
    println("The number of programs connected to 0 is ${zeroPool.size}")

    val pipesLeftToSort = (pipes - zeroPool).toMutableMap()
    var numberOfPools = 1

    while (pipesLeftToSort.isNotEmpty()) {
        pipesLeftToSort -= getPoolFor(pipesLeftToSort.entries.first().key, pipesLeftToSort)
        numberOfPools += 1
    }

    println("The number of pools is $numberOfPools")
}

private fun getPoolFor(program: Int, pipes: Map<Int, Set<Int>>): Set<Int> {
    val pool = mutableSetOf<Int>()
    var nextPipesToCheck = listOf(program)

    while (nextPipesToCheck.isNotEmpty()) {
        nextPipesToCheck = nextPipesToCheck.flatMap { pipes.getValue(it) }.filterNot { it in pool }
        pool.addAll(nextPipesToCheck)
    }

    return pool
}
