package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val map = linesOf("aoc2022/day12").flatMapIndexed { y, line ->
        line.mapIndexed { x, char -> (x to y) to char }
    }.toMap()

    val start = map.firstKeyByValue('S')
    val end = map.firstKeyByValue('E')
    val heightMap = map + mapOf(start to 'a', end to 'z')

    val distance = heightMap.minimalDistanceFrom(start, end)
    println("The minimal distance from the start to the end is $distance")

    val minimalDistanceFromBottom = heightMap.minimalDistanceTo(end)
    println("The minimal distance from the bottom to the end is $minimalDistanceFromBottom")
}


private fun Map<Point, Char>.minimalDistanceFrom(start: Point, end: Point): Int {
    return minimalDistance(start, { it == end }) { point ->
        val height = getValue(point)
        neighborsOf(point).filter { getValue(it).code <= height.code + 1 }.map { it to 1 }
    }
}

private fun Map<Point, Char>.minimalDistanceTo(end: Point): Int {
    val distances = mutableMapOf(end to 0)
    val nextToVisit = mutableListOf(end)

    while (nextToVisit.isNotEmpty()) {
        val current = nextToVisit.removeFirst()
        val height = getValue(current)

        if (height == 'a') return distances.getValue(current)

        neighborsOf(current).filter { it !in nextToVisit && getValue(it).code >= height.code - 1 }.forEach { neighbor ->
            distances[neighbor] = distances.getValue(current) + 1
            nextToVisit.add(neighbor)
        }
    }

    fail("No path found to the bottom of the map")
}

private fun Map<Point, Char>.neighborsOf(point: Point) = point.adjacentNeighbors.filter { it in keys }
