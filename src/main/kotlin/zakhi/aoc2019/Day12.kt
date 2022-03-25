package zakhi.aoc2019

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.orderedPairs
import zakhi.helpers.wholeNumbers
import kotlin.math.absoluteValue
import kotlin.math.sign


fun main() {
    val initialPositions = matchEachLineOf("aoc2019/day12", Regex("""<x=(-?\d+), y=(-?\d+), z=(-?\d+)>""")) { (x, y, z) ->
        listOf(x.toInt(), y.toInt(), z.toInt())
    }

    val totalEnergyAfter1000Steps = calculateTotalEnergy(initialPositions)
    println("The total energy is $totalEnergyAfter1000Steps")

    val numberOfStepsToRepeat = calculateTotalStepsToRepeat(initialPositions)
    println("The number of steps to repeat is $numberOfStepsToRepeat")
}


private fun calculateTotalEnergy(initialPositions: List<List<Int>>): Int {
    val moons = initialPositions.map { Moon(it) }
    repeat(1000) { applyGravity(moons) }
    return moons.sumOf { it.totalEnergy }
}

private fun calculateTotalStepsToRepeat(initialPosition: List<List<Int>>): Long {
    val moons = initialPosition.map { Moon(it) }
    val repeatsByAxis = mutableMapOf<Int, Long>()

    wholeNumbers().takeWhile { repeatsByAxis.size < 3 }.forEach { step ->
        applyGravity(moons)

        repeat(3) { axis ->
            if (axis !in repeatsByAxis && moons.all { it.isAtInitialPosition(axis) }) {
                repeatsByAxis[axis] = step.toLong()
            }
        }
    }

    return repeatsByAxis.values.reduce(Long::lcm)
}

private fun applyGravity(moons: List<Moon>) {
    moons.orderedPairs().forEach { (a, b) -> a.gravitateTowards(b) }
    moons.forEach { it.move() }
}

private fun Long.lcm(other: Long): Long = this / gcd(other) * other

private fun Long.gcd(other: Long): Long = when (other) {
    0L -> this
    else -> other.gcd(this % other)
}

private class Moon(
    private val initialPosition: List<Int>
) {
    private var position = initialPosition.toList()
    private var velocity = listOf(0, 0, 0)

    val totalEnergy get() = position.sumOf { it.absoluteValue } * velocity.sumOf { it.absoluteValue }

    fun gravitateTowards(other: Moon) {
        val velocityOffsets = position.zip(other.position).map { (my, others) -> (others - my).sign }
        velocity = velocity.sumWith(velocityOffsets)
    }

    fun move() {
        position = position.sumWith(velocity)
    }

    fun isAtInitialPosition(axis: Int) = position[axis] == initialPosition[axis] && velocity[axis] == 0
}

private fun List<Int>.sumWith(other: List<Int>): List<Int> = zip(other).map { it.toList().sum() }
