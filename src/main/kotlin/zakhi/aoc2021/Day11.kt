package zakhi.aoc2021

import zakhi.helpers.Point
import zakhi.helpers.allNeighbors
import zakhi.helpers.linesOf
import zakhi.helpers.wholeNumbers


fun main() {
    val octopuses = linesOf("aoc2021/day11").flatMapIndexed { y, line ->
        line.mapIndexed { x, num -> (x to y) to num.digitToInt() }
    }.toMap().let { Octopuses(it) }

    val totalFlashes = (1..100).fold(0) { total, _ -> total + octopuses.flash() }
    println("The total number of flashes is $totalFlashes")

    val allFlashingStep = wholeNumbers(from = 101).first { octopuses.flash() == octopuses.size }
    println("All octopuses flash after step $allFlashingStep")
}


private class Octopuses(initialEnergies: Map<Point, Int>) {
    private val octopuses = initialEnergies.toMutableMap()

    val size get() = octopuses.size

    fun flash(): Int {
        octopuses.forEach { increment(it.key) }
        val flashedPositions = mutableSetOf<Point>()

        while (true) {
            val (point, _) = octopuses.entries.find { (position, energy) -> position !in flashedPositions && energy > 9 } ?: break
            flashedPositions.add(point)
            point.allNeighbors.filter { it in octopuses }.forEach { increment(it) }
        }

        flashedPositions.forEach { octopuses[it] = 0 }
        return flashedPositions.size
    }

    private fun increment(position: Point) {
        octopuses[position] = octopuses.getValue(position) + 1
    }
}
