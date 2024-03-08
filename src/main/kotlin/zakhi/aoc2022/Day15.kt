package zakhi.aoc2022

import zakhi.helpers.*
import kotlin.math.absoluteValue


fun main() {
    val sensors = matchEachLineOf("aoc2022/day15", Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")) { (x1, y1, x2, y2) ->
        Sensor(x1.toInt() to y1.toInt(), x2.toInt() to y2.toInt())
    }

    val row = 2_000_000
    println("The number of non-beacon points in is ${countNonBeaconPoints(sensors, row)}")

    val range = 0..4_000_000
    val distressBeacon = findDistressBeacon(sensors, range)
    println("The tuning frequency of the distress beacon is ${distressBeacon.x * range.last.toLong() + distressBeacon.y}")
}


private fun countNonBeaconPoints(sensors: List<Sensor>, row: Int): Int {
    val nonBeaconPoints = sensors.mapNotNull { it.nonBeaconInRow(row) }.flatten().toSet()
    val numberOfBeaconsOnRow = sensors.map { it.closestBeacon }.filter { it.y == row }.distinct().count()

    return nonBeaconPoints.size - numberOfBeaconsOnRow
}

private fun findDistressBeacon(sensors: List<Sensor>, range: IntRange): Point {
    range.forEach { y ->
        val invalidXRanges = sensors.mapNotNull { it.nonBeaconInRow(y) }.filter { it.first in range || it.last in range }.sortedBy { it.first }
        val beaconXs = sensors.map { it.closestBeacon }.filter { it.y == y }.map { it.x }.toSet()

        var x = range.first

        while (x in range) {
            val matchingRange = invalidXRanges.firstOrNull { x in it }

            if (matchingRange == null) {
                when (x) {
                    in beaconXs -> x += 1
                    else -> return x to y
                }
            } else {
                x = matchingRange.last + 1
            }
        }
    }

    fail("Could not find distress beacon")
}

private data class Sensor(
    val position: Point,
    val closestBeacon: Point
) {
    private val distanceToBeacon get() = (position - closestBeacon).gridDistance

    fun nonBeaconInRow(row: Int): IntRange? {
        val distanceFromRow = (row - position.y).absoluteValue

        if (distanceFromRow > distanceToBeacon) {
            return null
        }

        val xOffset = distanceToBeacon - distanceFromRow
        return (position.x - xOffset .. position.x + xOffset)
    }
}
