package zakhi.aoc2018

import zakhi.aoc2018.Tool.*
import zakhi.helpers.*


fun main() {
    val input = entireTextOf("aoc2018/day22")
    val depth = Regex("""depth: (\d+)""").find(input)?.groupValues?.get(1)?.toInt() ?: fail("cannot find depth")
    val target = Regex("""target: (\d+),(\d+)""").find(input)?.groupValues?.drop(1)?.map { it.toInt() }?.toPoint() ?: fail("cannot find target")

    val cave = Cave(depth, target)

    val totalRiskLevel = grid(0..target.x, 0..target.y).sumOf { point -> cave.riskLevel(point) }
    println("The total risk level is $totalRiskLevel")
    println("The fastest time to target is ${cave.fastestTimeToTarget()}")
}


private class Cave(
    private val depth: Int,
    private val target: Point
) {
    private val erosionLevels = mutableMapOf<Point, Int>()

    fun riskLevel(point: Point): Int = erosionLevelFor(point).mod(3)

    fun fastestTimeToTarget(): Int = minimalDistance(
        start = (0 to 0) to Torch,
        isTarget = { (point, tool) -> point == target && tool == Torch },
        neighbors = { (point, tool) -> neighborsOf(point, tool) }
    )

    private fun erosionLevelFor(point: Point): Int {
        return erosionLevels.getOrPut(point) {
            val geoIndex = when {
                point == 0 to 0 || point == target -> 0L
                point.y == 0 -> point.x * 16807L
                point.x == 0 -> point.y * 48271L
                else -> erosionLevelFor(point + (-1 to 0)).toLong() * erosionLevelFor(point + (0 to -1))
            }

            (geoIndex + depth).mod(20183)
        }
    }

    fun neighborsOf(point: Point, tool: Tool): List<Pair<PointWithTool, Int>> {
        val otherTool = validToolsFor(riskLevel(point)).first { it != tool }
        val neighbors = point.adjacentNeighbors.filterNot { it.x < 0 || it.y < 0 }.filter { tool in validToolsFor(riskLevel(it)) }

        return neighbors.map { (it to tool) to 1 } + ((point to otherTool) to 7)
    }

    fun validToolsFor(type: Int) = when (type) {
        0 -> listOf(ClimbingGear, Torch)
        1 -> listOf(ClimbingGear, Neither)
        2 -> listOf(Torch, Neither)
        else -> fail("Invalid type $type")
    }
}

private typealias PointWithTool = Pair<Point, Tool>

private enum class Tool {
    Neither,
    ClimbingGear,
    Torch
}
