package zakhi.aoc2015

import zakhi.entireTextOf
import zakhi.md5Hash
import zakhi.naturalNumbers


fun main() {
    val key = entireTextOf("aoc2015/day4")

    val firstFiveZeroes = naturalNumbers().first { "$key$it".md5Hash().startsWith("00000") }
    println("The first occurrence of 5-zero start is at $firstFiveZeroes")

    val firstSixZeroes = naturalNumbers().first { "$key$it".md5Hash().startsWith("000000") }
    println("The first occurrence of 6-zero start is at $firstSixZeroes")
}
