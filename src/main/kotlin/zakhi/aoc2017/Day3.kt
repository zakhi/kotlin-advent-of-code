package zakhi.aoc2017

import zakhi.helpers.*


fun main() {
    val input = entireTextOf("aoc2017/day3").trim().toInt()

    val distance = spiral().withIndex().first { (index, _) -> index + 2 == input }.value.gridDistance
    println("The number of steps required is $distance")

    val center = 0 to 0
    val values = mutableMapOf(center to 1).withDefault { 0 }

    val value = spiral().map { point ->
        point.allNeighbors.sumOf { values.getValue(it) }.also { newValue -> values[point] = newValue }
    }.first { it > input }

    println("The value written is $value")
}


private fun spiral() = sequence {
    var point = 0 to 0

    suspend fun SequenceScope<Point>.move(offset: Point) {
        point += offset
        yield(point)
    }

    wholeNumbers().filter { it.isEven }.forEach { loop ->
        move(1 to 0)
        repeat(loop - 1) { move(0 to 1) }
        repeat(loop) { move(-1 to 0) }
        repeat(loop) { move(0 to -1) }
        repeat(loop) { move(1 to 0) }
    }
}
