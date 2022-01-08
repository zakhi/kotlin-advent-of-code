package zakhi.aoc2019

import zakhi.helpers.entireTextOf


fun main() {
    val range = entireTextOf("aoc2019/day4").trim().split("-").map { it.toInt() }.let { (start, end) -> start..end }

    val matchingPasswords = range.filter { it.matchesPasswordCriteria() }
    println("The number of matching passwords are ${matchingPasswords.count()}")

    val actualMatchingPasswords = matchingPasswords.filter { it.containsExactPair() }
    println("The number of actual matching passwords are ${actualMatchingPasswords.count()}")
}

private fun Int.matchesPasswordCriteria(): Boolean {
    val charPairs = toString().toCharArray().toList().zipWithNext()
    return charPairs.any { (a, b) -> a == b } && charPairs.all { (a, b) -> a <= b }
}

private fun Int.containsExactPair(): Boolean =
    Regex("""(\d)\1+""").findAll(toString()).any { it.value.length == 2 }
