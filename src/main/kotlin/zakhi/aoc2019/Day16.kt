@file:OptIn(ExperimentalTime::class)

package zakhi.aoc2019

import zakhi.helpers.entireTextOf
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime


fun main() {
    val input = entireTextOf("aoc2019/day16").trim()

    val signal = input.toByteArray()
    transform(signal)
    println("The first eight digits are ${signal.firstEightDigits()}")

    val skip = input.take(7).toInt()
    val realSignal = input.toByteArray(10000, skip)
    transform(realSignal, skip)

    println("The first eight digits of the real signal are ${realSignal.firstEightDigits()}")
}


private fun String.toByteArray(duplicate: Int = 1, skip: Int = 0): ByteArray =
    ByteArray(length * duplicate - skip).also { array ->
        array.indices.forEach { index ->
            array[index] = get((index + skip).mod(length)).digitToInt().toByte()
        }
    }

private fun ByteArray.firstEightDigits() = take(8).joinToString("")

private fun transform(input: ByteArray, skip: Int = 0) {
    if (skip >= input.size) {
        repeat(100) { simpleTransform(input) }
    } else {
        repeat(100) { regularTransform(input, skip) }
    }
}

private fun simpleTransform(input: ByteArray) {
    var lastDigit = input.first()
    input[0] = input.sum().mod(10).toByte()

    input.indices.drop(1).forEach { index ->
        val digitToSave = input[index]
        input[index] = (10 + input[index - 1] - lastDigit).mod(10).toByte()
        lastDigit = digitToSave
    }
}

private fun regularTransform(input: ByteArray, skip: Int) {
    input.indices.forEach { index ->
        val repeatSize = index + skip + 1
        val sum = input.asSequence().drop(index).zip(pattern(repeatSize)).sumOf { (value, multiplier) -> value * multiplier }
        input[index] = sum.absoluteValue.mod(10).toByte()
    }
}

private fun pattern(repeatSize: Int) = sequence {
    while (true) {
        listOf(1, 0, -1, 0).forEach { value ->
            repeat(repeatSize) { yield(value) }
        }
    }
}
