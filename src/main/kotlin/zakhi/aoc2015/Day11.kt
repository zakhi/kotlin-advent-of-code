package zakhi.aoc2015

import zakhi.helpers.entireTextOf


fun main() {
    val currentPassword = entireTextOf("aoc2015/day11")

    val nextPassword = findNextPassword(currentPassword)
    println("The next password is $nextPassword")

    val secondNextPassword = findNextPassword(nextPassword)
    println("The second next password is $secondNextPassword")
}


private fun findNextPassword(initialPassword: String) =
    generateSequence(initialPassword.increment()) { it.increment() }.first { it.isValidPassword }

private val String.isValidPassword: Boolean get() {
    val hasConsecutiveTriplet = tripletSubstrings.any { it.allCharsAreConsecutive }
    val hasNonOverlappingPairs = Regex("""(\w)\1""").findAll(this).map { it.value }.distinct().count() > 1

    return hasConsecutiveTriplet && hasNonOverlappingPairs
}

private val String.tripletSubstrings: List<String> get() = (0 until lastIndex - 2).map { slice(it..it + 2) }

private val String.allCharsAreConsecutive: Boolean get() =
    zipWithNext().all { (first, second) -> first.inc() == second }

private fun String.increment(): String {
    if (isEmpty()) return "a"

    val newLast = last().increment()
    val start = dropLast(1)

    return if (newLast == 'a') start.increment() + newLast else start + newLast
}

private fun Char.increment(): Char = when (this) {
    'z' -> 'a'
    'h', 'k', 'n' -> inc().inc()
    else -> inc()
}
