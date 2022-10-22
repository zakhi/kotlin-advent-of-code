package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.properties.Delegates.notNull


fun main() {
    val pointsAffected = grid(0 until 50).filter { it.pulled() }.toSet()
    println("There number of points affected by the beam is ${pointsAffected.size}")

    var point = pointsAffected.first { it.x > 10 && it.y > 10 }

    while (true) {
        val horizontalRange = horizontalBeamRange(point)
        val verticalRange = verticalBeamRange(point)

        if (horizontalRange.size >= 100 && verticalRange.size >= 100) {
            break
        }

        if (horizontalRange.size < 100) point = nextHorizontalBeamPoint(point + (0 to 1))
        if (verticalRange.size < 100) point = nextVerticalBeamPoint(point + (1 to 0))
    }

    println("The closest point to emitter is coded as ${point.x * 10000 + point.y}")
}

private val program = readProgramFrom(entireTextOf("aoc2019/day19"))
private val cache = mutableMapOf<Point, Boolean>()

private fun Point.pulled(): Boolean = cache.getOrPut(this) {
    val coordinates = mutableListOf(x, y)
    var output by notNull<Long>()

    IntcodeComputer(program, inputProvider = { coordinates.removeFirst().toLong() }, outputConsumer = { output = it }).start()
    output > 0
}

private fun horizontalBeamRange(origin: Point): IntRange {
    val end = wholeNumbers(from = origin.x).takeWhile { (it to origin.y).pulled() }.last()
    return origin.x .. end
}

private fun verticalBeamRange(origin: Point): IntRange {
    val end = wholeNumbers(from = origin.y).takeWhile { (origin.x to it).pulled() }.last()
    return origin.y .. end
}

private fun nextHorizontalBeamPoint(origin: Point): Point =
    wholeNumbers(from = origin.x).first { (it to origin.y).pulled() } to origin.y

private fun nextVerticalBeamPoint(origin: Point): Point =
    origin.x to wholeNumbers(from = origin.y).first { (origin.x to it).pulled() }


private val IntRange.size get() = endInclusive - start + 1
