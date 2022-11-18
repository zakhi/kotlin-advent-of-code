package zakhi.aoc2018

import zakhi.aoc2018.Acre.*
import zakhi.helpers.Point
import zakhi.helpers.allNeighbors
import zakhi.helpers.fail
import zakhi.helpers.linesOf


fun main() {
    val initialLand = linesOf("aoc2018/day18").flatMapIndexed { y, line ->
        line.mapIndexed { x, char -> (x to y) to acreFrom(char) }
    }.toMap()

    val landAfter10Minutes = (1..10).fold(initialLand) { land, _ -> transform(land) }
    println("Total lumber after 10 minutes is ${landAfter10Minutes.totalWood}")

    val previousLands = mutableMapOf(initialLand to 0)
    var currentLand = initialLand

    for (i in 1..1_000_000_000) {
        currentLand = transform(currentLand)
        if (currentLand in previousLands) break
        previousLands[currentLand] = previousLands.size
    }

    val firstRepeated = previousLands.getValue(currentLand)
    val repeatedPosition = (1_000_000_000 - firstRepeated).mod(previousLands.size - firstRepeated) + firstRepeated

    val landAfter1BMinutes = previousLands.entries.first { it.value == repeatedPosition }.key
    println("Total lumber after 1B minutes is ${landAfter1BMinutes.totalWood}")

}

private typealias Land = Map<Point, Acre>

private val Land.totalWood get() = count { it.value == Lumberyard } * count { it.value == Trees }

private fun transform(land: Land): Land = land.mapValues { (point, acre) ->
    val adjacentCounts = point.allNeighbors.mapNotNull { land[it] }.groupingBy { it }.eachCount().withDefault { 0 }

    when (acre) {
        OpenGround -> if (adjacentCounts.getValue(Trees) >= 3) Trees else OpenGround
        Trees -> if (adjacentCounts.getValue(Lumberyard) >= 3) Lumberyard else Trees
        Lumberyard -> if (adjacentCounts.getValue(Lumberyard) >= 1 && adjacentCounts.getValue(Trees) >= 1) Lumberyard else OpenGround
    }
}

private enum class Acre {
    OpenGround, Trees, Lumberyard
}

private fun acreFrom(char: Char): Acre = when (char) {
    '.' -> OpenGround
    '|' -> Trees
    '#' -> Lumberyard
    else -> fail("Unknown char $char")
}
