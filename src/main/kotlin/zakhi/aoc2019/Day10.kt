package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.math.atan2


fun main() {
    val asteroids = linesOf("aoc2019/day10").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') x to y else null }
    }.toList()

    val detectableCounts = asteroids.associateWith { p ->
        asteroids.filterNot { it == p }.map { other -> p.angleTo(other) }.distinct().count()
    }

    val (station, mostDetected) = detectableCounts.entries.maxBy { it.value }
    println("The number of detected asteroids is $mostDetected")

    val vaporizedAsteroids = vaporizationSequence(asteroids, station)
    val vaporized200 = vaporizedAsteroids.take(200).last()

    println("The value for the 200th vaporized asteroid is ${vaporized200.x * 100 + vaporized200.y}")
}


private fun vaporizationSequence(asteroids: List<Point>, station: Point) = sequence {
    val asteroidsByAngle = asteroids.filterNot { it == station }.groupBy { it.angleTo(station) }
        .mapValues { it.value.toMutableList() }.toSortedMap()

    while (true) {
        val nextToVaporize = asteroidsByAngle.mapNotNull { it.value.removeFirstOrNull() }
        if (nextToVaporize.isEmpty()) break
        yieldAll(nextToVaporize)
    }
}

private fun Point.angleTo(other: Point): Double = -atan2((x - other.x).toDouble(), (y - other.y).toDouble())
