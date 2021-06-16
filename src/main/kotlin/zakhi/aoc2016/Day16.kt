package zakhi.aoc2016

import zakhi.input.entireTextOf
import zakhi.numbers.isEven
import zakhi.strings.join


fun main() {
    val initialState = entireTextOf("aoc2016/day16").trim()

    val disc = fillDiscFrom(initialState, size = 272)
    println("The checksum is ${disc.checksum()}")

    val secondDisc = fillDiscFrom(initialState, size = 35651584)
    println("The checksum is ${secondDisc.checksum()}")
}


private fun fillDiscFrom(state: String, size: Int): String {
    val result = buildString {
        append(state)
        append("0")
        state.reversed().forEach { if (it == '0') append('1') else append('0') }
    }

    return if (result.length < size) fillDiscFrom(result, size) else result.take(size)
}

private fun String.checksum(): String {
    val checksum = chunked(2).join { pair -> if (pair.toSet().size == 1) "1" else "0" }
    return if (checksum.length.isEven) checksum.checksum() else checksum
}


