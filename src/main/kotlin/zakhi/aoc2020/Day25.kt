package zakhi.aoc2020

import zakhi.helpers.linesOf
import zakhi.helpers.second
import zakhi.helpers.wholeNumbers


fun main() {
    val keys = linesOf("aoc2020/day25").map { it.toLong() }.toList()
    val loopSizes = keys.map { findLoopSizeOf(it) }

    val encryptionKey = transform(keys.first(), loopSizes.second())
    println("The encryption key is $encryptionKey")
}


private fun findLoopSizeOf(key: Long): Long {
    var value = 1L
    return wholeNumbers().onEach { value = transformStep(value) }.first { value == key }.toLong()
}

private fun transform(subject: Long, loopSize: Long): Long =
    (1..loopSize).fold(1L) { value, _ -> transformStep(value, subject) }

private fun transformStep(value: Long, subject: Long = 7L) = (value * subject).mod(20201227L)
