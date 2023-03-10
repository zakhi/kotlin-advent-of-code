package zakhi.aoc2021

import zakhi.helpers.linesOf


fun main() {
    val depths = linesOf("aoc2021/day1").map { it.toInt() }

    val increases = depths.countIncreases()
    println("The number of depth measurement increases is $increases")

    val windowIncreases = depths.windowed(size = 3, partialWindows = false) { it.sum() }.countIncreases()
    println("The number of window depth measurement increases is $windowIncreases")
}


private fun Sequence<Int>.countIncreases() = zipWithNext().count { (previous, current) -> current > previous }
