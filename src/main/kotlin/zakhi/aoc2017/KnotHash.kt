package zakhi.aoc2017

import kotlin.streams.toList


fun knotHash(input: String): List<UByte> {
    val lengths = input.chars().toList() + listOf(17, 31, 73, 47, 23)
    val result = knotTransform(lengths, times = 64)

    return result.chunked(16).flatMap { codes ->
        listOf(codes.map { it.toUByte() }.reduce { a, b -> a xor b })
    }.toList()
}


fun knotTransform(lengths: List<Int>, times: Int = 1): List<Int> {
    val list = CircularMutableList()
    var skip = 0
    var position = 0

    repeat(times) {
        for (length in lengths) {
            position = list.reverse(position, length) + skip
            skip += 1
        }
    }

    return list.values
}


private class CircularMutableList {
    private val list = (0..255).toMutableList()

    val values: List<Int> get() = list

    fun reverse(start: Int, length: Int): Int {
        (0 until length / 2).map { start + it to start + length - it - 1 }.forEach { (first, second) ->
            val firstValue = this[first]
            this[first] = this[second]
            this[second] = firstValue
        }

        return proper(start + length)
    }

    private operator fun set(index: Int, value: Int) {
        list[proper(index)] = value
    }

    private operator fun get(index: Int): Int = list[proper(index)]

    private fun proper(index: Int): Int = index.mod(list.size)
}
