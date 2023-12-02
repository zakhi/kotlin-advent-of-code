package zakhi.aoc2021

import zakhi.helpers.*
import kotlin.math.absoluteValue
import kotlin.math.max


fun main() {
    val (minX, maxX, minY, maxY) = entireTextOf("aoc2021/day17").let { text ->
        Regex("""-?\d+""").findAll(text).map { it.value.toInt() }.toList()
    }

    val targetArea = TargetArea(minX..maxX, minY..maxY)

    println("The highest distance is ${targetArea.highestDistance}")
    println("The number of initial velocities hitting the target area is ${targetArea.hittingInitialVelocities.size}")
}

private typealias Velocity = Point

private class TargetArea(
    private val xs: IntRange,
    private val ys: IntRange
) {
    val highestDistance get() = ys.first.absoluteValue * (ys.first.absoluteValue - 1) / 2

    val hittingInitialVelocities: List<Velocity> get() =
        xVelocities.product(yVelocities).filter { it.isHitting() }.toList()

    private fun Velocity.isHitting(): Boolean {
        var point = 0 to 0
        var velocity = this

        while (!point.misses()) {
            point += velocity
            if (point.hits()) return true

            velocity = max(velocity.x - 1, 0) to velocity.y - 1
        }

        return false
    }

    private val yVelocities get() = (ys.first ..< ys.first.absoluteValue).toList()
    private val xVelocities get() = (xs.first.floorSqrt() .. xs.last).toList()

    private fun Point.hits(): Boolean = x in xs && y in ys
    private fun Point.misses(): Boolean = x > xs.last || y < ys.first
}
