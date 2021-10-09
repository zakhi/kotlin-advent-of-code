package zakhi.aoc2018

import zakhi.helpers.*


fun main() {
    val coordinates = matchEachLineOf("aoc2018/day6", Regex("""(\d+), (\d+)""")) { (x, y) -> x.toInt() to y.toInt() }
    val grid = Grid(coordinates)

    println("The largest finite area is ${grid.largestFiniteArea}")
    println("The number of close locations is ${grid.pointsWithTotalDistanceUnder(10_000)}")
}


private class Grid(coordinates: List<Point>) {
    private val xs = coordinates.minOf { it.x } .. coordinates.maxOf { it.x }
    private val ys = coordinates.minOf { it.y } .. coordinates.maxOf { it.y }
    private val distances = grid(xs, ys).map { point -> Distances(point, coordinates) }.filter { it.closestCoordinate != null }

    val largestFiniteArea: Int get() {
        val coordinatesWithInfiniteAreas = distances.filter { it.point.isOnEdge }.map { it.closestCoordinate }.toSet()

        return distances.filter { it.closestCoordinate !in coordinatesWithInfiniteAreas }
            .groupingBy { it.closestCoordinate }.eachCount()
            .maxOf { it.value }
    }

    fun pointsWithTotalDistanceUnder(limit: Int): Int = distances.count { it.totalDistanceFromAllCoordinates < limit }

    private val Point.isOnEdge: Boolean get() = x == xs.first || x == xs.last || y == ys.first || y == ys.last
}

private class Distances(
    val point: Point,
    coordinates: List<Point>
) {
    val distances = coordinates.associateWith { (it - point).gridDistance }

    val closestCoordinate: Point? get() {
        val minimalDistance = distances.minOf { it.value }
        return distances.filterValues { it == minimalDistance }.keys.takeIf { it.size == 1 }?.first()
    }

    val totalDistanceFromAllCoordinates: Int get() = distances.values.sum()
}
