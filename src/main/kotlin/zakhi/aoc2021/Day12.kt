package zakhi.aoc2021

import zakhi.helpers.linesOf


fun main() {
    val input = linesOf("aoc2021/day12").map { it.split("-").toSet() }.toList()
    val nodes = input.flatMap { it.toList() }.distinct()

    val passages = nodes.associateWith { node ->
        input.filter { node in it }.map { connection -> connection.first { it != node } }
    }

    val paths = passages.countPaths(from = "start")
    println("The total number of paths is $paths")

    val flexiblePaths = passages.countFlexiblePaths(from = "start")
    println("The total number of flexible paths is $flexiblePaths")
}


private fun Map<String, List<String>>.countPaths(from: String, visited: Set<String> = emptySet()): Int {
    if (from == "end") return 1
    if (from.isLowerCase() && from in visited) return 0

    val updatedVisited = visited + from
    return getValue(from).sumOf { node -> countPaths(from = node, updatedVisited) }
}

private fun Map<String, List<String>>.countFlexiblePaths(from: String, visited: Map<String, Int> = emptyMap()): Int {
    if (from == "end") return 1
    if (from == "start" && from in visited) return 0

    val updatedVisited = visited + mapOf(from to visited.getOrDefault(from, 0) + 1)

    val smallCaveVisits = updatedVisited.filter { it.key.isLowerCase() }
    if (smallCaveVisits.any { it.value > 2 } || smallCaveVisits.count { it.value == 2 } > 1) return 0

    return getValue(from).sumOf { node -> countFlexiblePaths(from = node, updatedVisited) }
}

private fun String.isLowerCase() = all { it.isLowerCase() }
