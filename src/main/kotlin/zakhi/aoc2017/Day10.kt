package zakhi.aoc2017

import zakhi.helpers.entireTextOf
import zakhi.helpers.join
import kotlin.experimental.xor
import kotlin.streams.toList


fun main() {
    val input = entireTextOf("aoc2017/day10").trim()

    val lengths = input.split(",").map { it.toInt() }
    val list = CircularMutableList()

    process(list, lengths)
    println("The multiplication of the first two numbers is ${list.first * list.second}")

    val asciiLengths = input.chars().toList() + listOf(17, 31, 73, 47, 23)
    val asciiList = CircularMutableList()

    process(asciiList, asciiLengths, times = 64)
    println("The hash is ${asciiList.hash}")
}


private fun process(list: CircularMutableList, lengths: List<Int>, times: Int = 1) {
    var skip = 0
    var position = 0

    repeat(times) {
        for (length in lengths) {
            position = list.reverse(position, length) + skip
            skip += 1
        }
    }
}


private class CircularMutableList {
    private val list = (0..255).toMutableList()

    val first: Int get() = list[0]
    val second: Int get() = list[1]

    fun reverse(start: Int, length: Int): Int {
        (0 until length / 2).map { start + it to start + length - it - 1 }.forEach { (first, second) ->
            val firstValue = this[first]
            this[first] = this[second]
            this[second] = firstValue
        }

        return proper(start + length)
    }

    val hash: String get() = list.chunked(16).flatMap { codes ->
        listOf(codes.map { it.toByte() }.reduce { a, b -> a xor b })
    }.join { "%02x".format(it) }

    private operator fun set(index: Int, value: Int) {
        list[proper(index)] = value
    }

    private operator fun get(index: Int): Int = list[proper(index)]

    private fun proper(index: Int): Int = index.mod(list.size)
}
