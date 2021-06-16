package zakhi.aoc2015

import zakhi.helpers.entireTextOf
import zakhi.helpers.wholeNumbers
import zakhi.helpers.md5Hash


fun main() {
    val key = entireTextOf("aoc2015/day4")

    val firstFiveZeroes = wholeNumbers().first { "$key$it".md5Hash().startsWith("00000") }
    println("The first occurrence of 5-zero start is at $firstFiveZeroes")

    val firstSixZeroes = wholeNumbers().first { "$key$it".md5Hash().startsWith("000000") }
    println("The first occurrence of 6-zero start is at $firstSixZeroes")
}
