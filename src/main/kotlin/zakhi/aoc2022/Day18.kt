package zakhi.aoc2022

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.pairs
import kotlin.math.absoluteValue


fun main() {
    val cubes = matchEachLineOf("aoc2022/day18", Regex("""(\d+),(\d+),(\d+)""")) { (x, y, z) ->
        TriPoint(x.toInt(), y.toInt(), z.toInt())
    }.toList()

    val surfaceArea = findSurfaceArea(cubes)
    println("The total surface area of the cubes is $surfaceArea")

    val closedCubes = calculateClosedCubes(cubes)
    val actualSurfaceArea = findSurfaceArea(closedCubes)
    println("The actual surface area of the cubes is $actualSurfaceArea")
}


private fun findSurfaceArea(cubes: List<TriPoint>): Int {
    val neighbors = cubes.associateWith<TriPoint, MutableList<TriPoint>> { mutableListOf() }

    cubes.pairs().forEach { (a, b) ->
        if (a.distanceFrom(b) == 1) {
            neighbors.getValue(a).add(b)
            neighbors.getValue(b).add(a)
        }
    }
    return cubes.sumOf { 6 - neighbors.getValue(it).size }
}

private fun calculateClosedCubes(cubes: List<TriPoint>): List<TriPoint> {
    val area = TriPointArea(cubes)

    val queue = area.faceCubes().filter { it !in cubes }.toMutableList()
    val visited = mutableSetOf<TriPoint>()

    while (queue.isNotEmpty()) {
        val cube = queue.removeFirst()
        visited.add(cube)

        cube.neighbors().filter { it in area && it !in visited && it !in cubes }.forEach { neighbor ->
            queue.add(neighbor)
        }
    }

    return area.allCubes().filter { it in cubes || it !in visited }
}

private typealias TriPoint = Triple<Int, Int, Int>

private fun TriPoint.distanceFrom(other: TriPoint): Int = toList().zip(other.toList()).sumOf { (a, b) -> (a - b).absoluteValue }

private fun TriPoint.neighbors(): List<TriPoint> = listOf(
    TriPoint(first + 1, second, third),
    TriPoint(first - 1, second, third),
    TriPoint(first, second + 1, third),
    TriPoint(first, second - 1, third),
    TriPoint(first, second, third + 1),
    TriPoint(first, second, third - 1)
)

private class TriPointArea(points: List<TriPoint>) {
    private val xs = points.map { it.first }.progression()
    private val ys = points.map { it.second }.progression()
    private val zs = points.map { it.third }.progression()

    fun faceCubes(): List<TriPoint> {
        val xPoints = listOf(xs.first, xs.last).flatMap { x -> ys.flatMap { y -> zs.map { z -> TriPoint(x, y, z) } } }
        val yPoints = listOf(ys.first, ys.last).flatMap { y -> xs.flatMap { x -> zs.map { z -> TriPoint(x, y, z) } } }
        val zPoints = listOf(zs.first, zs.last).flatMap { z -> xs.flatMap { x -> ys.map { y -> TriPoint(x, y, z) } } }

        return (xPoints + yPoints + zPoints).distinct()
    }

    fun allCubes(): List<TriPoint> = xs.flatMap { x -> ys.flatMap { y -> zs.map { z -> TriPoint(x, y, z) } } }

    operator fun contains(point: TriPoint): Boolean = point.first in xs && point.second in ys && point.third in zs
}

private fun List<Int>.progression() = IntProgression.fromClosedRange(min(), max(), 1)
