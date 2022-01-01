package zakhi.aoc2019

import zakhi.helpers.linesOf


fun main() {
    val masses = linesOf("aoc2019/day1").map { it.toInt() }.toList()

    val fuel = masses.sumOf { fuelFor(it) }
    println("The fuel required is $fuel")

    val totalFuel = masses.sumOf { accumulativeFuelFor(it) }
    println("The total fuel required is $totalFuel")
}


private fun fuelFor(mass: Int) = mass / 3 - 2

private fun accumulativeFuelFor(mass: Int): Int {
    val fuel = fuelFor(mass)
    if (fuel <= 0) return 0

    return fuel + accumulativeFuelFor(fuel)
}
