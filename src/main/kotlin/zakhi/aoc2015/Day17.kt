package zakhi.aoc2015

import zakhi.linesOf


fun main() {
    val containers = linesOf("aoc2015/day17").map { it.toInt() }.sorted().toList()

    val combinations = combinationsOf(containers, sum = 150)
    println("There are ${combinations.count()} container combinations which sum to 150")

    val minimumContainers = combinations.minOf { it.count() }
    val minimalCombinations = combinations.count { it.size == minimumContainers }
    println("There are $minimalCombinations minimal container combinations")
}

fun combinationsOf(containers: List<Int>, sum: Int): List<List<Int>> =
    containers.takeWhile { it <= sum }.flatMapIndexed { index, container ->
        if (container == sum) {
            listOf(listOf(container))
        } else {
            combinationsOf(containers.drop(index + 1), sum - container).map { it + container }
        }
    }
