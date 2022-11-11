package zakhi.aoc2020

import zakhi.helpers.linesOf
import zakhi.helpers.second


fun main() {
    val lines = linesOf("aoc2020/day13").toList()
    val startTime = lines.first().toInt()
    val buses = lines.second().split(",").withIndex().filterNot { it.value == "x" }.map { it.value.toLong() to it.index.toLong() }

    val (busId, minutesToWait) = buses.map { (bus, _) -> bus to bus - startTime.mod(bus) }.minBy { it.second }
    println("The ID of the earliest bus times the minutes to wait is ${busId * minutesToWait}")

    val (_, earliestTime) = buses.reduce { (power, offset), (bus, index) ->
        val multiple = (0L until bus).first { i -> (offset + power * i + index).mod(bus) == 0L }
        power * bus to offset + power * multiple
    }

    println("The earliest time the buses will come in sequence is $earliestTime")
}
