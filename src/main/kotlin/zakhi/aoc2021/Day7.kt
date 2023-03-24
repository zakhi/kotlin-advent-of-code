package zakhi.aoc2021

import zakhi.helpers.entireTextOf
import kotlin.math.absoluteValue


fun main() {
    val crabPositions = entireTextOf("aoc2021/day7").trim().split(",").map { it.toInt() }
    val positions = crabPositions.min()..crabPositions.max()

    val leastFuelSpent = positions.minOf { crabPositions.fuelSpentTo(it) }
    println("The smallest amount of fuel spent is $leastFuelSpent")

    val leastFuelSpentWithFasterBurnRate = positions.minOf { crabPositions.fuelSpentTo(it, fasterBurnRate = true) }
    println("The smallest amount of fuel spent with faster burn rate is $leastFuelSpentWithFasterBurnRate")
}


private fun List<Int>.fuelSpentTo(position: Int, fasterBurnRate: Boolean = false): Int = sumOf {
    val distance = (it - position).absoluteValue
    if (fasterBurnRate) distance * (distance + 1) / 2 else distance
}
