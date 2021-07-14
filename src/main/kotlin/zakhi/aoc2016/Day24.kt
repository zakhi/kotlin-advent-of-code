package zakhi.aoc2016

import zakhi.helpers.Point
import zakhi.helpers.adjacentNeighbors
import zakhi.helpers.linesOf
import zakhi.helpers.permutations


fun main() {
    val map = linesOf("aoc2016/day24").withIndex().flatMap { (y, row) ->
        row.mapIndexed { x, char -> (x to y) to char }
    }.toMap().withDefault { '#' }

    val nodes = map.values.filter { it.isDigit() }
    val distances = calculateDistances(map, nodes)

    val possibleRoutes = nodes.filterNot { it == '0' }.permutations().map { listOf('0') + it }
    val shortestRoute = possibleRoutes.minOf { route -> distanceOf(route, distances) }
    println("The shortest route length is $shortestRoute")

    val closedRoutes = possibleRoutes.map { it + '0' }
    val shortedClosedRoute = closedRoutes.minOf { route -> distanceOf(route, distances) }
    println("The shortest closed route length is $shortedClosedRoute")
}


private fun calculateDistances(map: Map<Point, Char>, nodes: List<Char>): Map<Pair<Char, Char>, Int> {
    val nodeDistances = mutableMapOf<Pair<Char, Char>, Int>()
    val remainingNodes = nodes.toMutableList()

    while (remainingNodes.size > 1) {
        val node = remainingNodes.removeFirst()

        var positions = listOf(map.entries.first { it.value == node }.key)
        val visited = positions.toMutableSet()
        var distance = 1

        while (remainingNodes.any { (node to it) !in nodeDistances }) {
            positions = positions.flatMap { it.adjacentNeighbors }.distinct().filter { map.getValue(it) != '#' && it !in visited }

            positions.forEach {
                visited.add(it)
                val char = map.getValue(it)

                if (char.isDigit()) {
                    nodeDistances[node to char] = distance
                    nodeDistances[char to node] = distance
                }
            }

            distance += 1
        }
    }

    return nodeDistances
}

private fun distanceOf(route: List<Char>, distances: Map<Pair<Char, Char>, Int>): Int =
    route.zipWithNext().fold(0) { distance, nodes -> distance + distances.getValue(nodes) }
