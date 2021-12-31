package zakhi.aoc2018

import zakhi.helpers.combinations
import zakhi.helpers.matchEachLineOf
import kotlin.math.absoluteValue


fun main() {
    val points = matchEachLineOf("aoc2018/day25", Regex("""(-?\d+),(-?\d+),(-?\d+),(-?\d+)""")) { FixedPoint(it.toList().map { coordinate -> coordinate.toInt() }) }
    val neighbors = calculateNeighbors(points).withDefault { emptySet() }
    val groups = findGroups(points, neighbors)

    println("The number of groups is ${groups.size}")
}

private fun calculateNeighbors(points: List<FixedPoint>): Map<FixedPoint, Set<FixedPoint>> = buildMap<FixedPoint, MutableSet<FixedPoint>> {
    points.combinations(2).filter { (a, b) -> a intersectsWith b }.forEach { (a, b) ->
        getOrPut(a) { mutableSetOf() }.add(b)
        getOrPut(b) { mutableSetOf() }.add(a)
    }
}

private fun findGroups(
    points: List<FixedPoint>,
    neighbors: Map<FixedPoint, Set<FixedPoint>>
): Set<Set<FixedPoint>> = buildSet {
    val remainingPoints = points.toMutableList()

    while (remainingPoints.isNotEmpty()) {
        val currentGroup = mutableSetOf<FixedPoint>()
        addToGroup(currentGroup, remainingPoints.first(), neighbors)

        add(currentGroup)
        remainingPoints.removeAll(currentGroup)
    }
}

private fun addToGroup(group: MutableSet<FixedPoint>, point: FixedPoint, neighbors: Map<FixedPoint, Set<FixedPoint>>) {
    if (point in group) return

    group.add(point)
    neighbors.getValue(point).forEach { neighbor -> addToGroup(group, neighbor, neighbors) }
}


private data class FixedPoint(val coordinates: List<Int>) {
    infix fun intersectsWith(other: FixedPoint): Boolean =
        coordinates.zip(other.coordinates).sumOf { (a, b) -> (a - b).absoluteValue } <= 3
}
