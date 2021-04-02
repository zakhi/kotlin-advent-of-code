package zakhi.aoc2015

import zakhi.entireTextOf
import java.math.BigInteger
import java.security.MessageDigest


fun main() {
    val key = entireTextOf("aoc2015/day4")

    val firstFiveZeroes = naturalNumbers.first { "$key$it".md5Hash().startsWith("00000") }
    println("The first occurrence of 5-zero start is at $firstFiveZeroes")

    val firstSixZeroes = naturalNumbers.first { "$key$it".md5Hash().startsWith("000000") }
    println("The first occurrence of 6-zero start is at $firstSixZeroes")
}


private val naturalNumbers = generateSequence(1) { it + 1 }

private fun String.md5Hash(): String {
    val md5 = MessageDigest.getInstance("MD5")
    return BigInteger(1, md5.digest(toByteArray())).toString(16).padStart(32, '0')
}
