package zakhi.aoc2015

import zakhi.helpers.entireTextOf


fun main() {
    println("Santa is taken to floor $destinationFloor")
    println("Santa entered the basement on position $basementEntryPosition")
}


private val input by lazy { entireTextOf("aoc2015/day1") }

private val destinationFloor: Int get() = input.sumOf(::floorOffset)

private val basementEntryPosition: Int get() {
    var currentFloor = 0

    input.forEachIndexed { index, char ->
        currentFloor += floorOffset(char)
        if (currentFloor == -1) return index + 1
    }

    throw Exception("Santa never reached the basement")
}

private fun floorOffset(char: Char): Int = when (char) {
    '(' -> 1
    ')' -> -1
    else -> throw Exception("Unexpected character $char")
}
