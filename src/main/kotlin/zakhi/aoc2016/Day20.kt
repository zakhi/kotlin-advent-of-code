package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf


fun main() {
    val blacklist = matchEachLineOf("aoc2016/day20", Regex("""(\d+)-(\d+)""")) { (start, end) ->
        start.toUInt()..end.toUInt()
    }.sortedBy { it.first }

    println("The first allowed address is ${blacklist.firstAllowed}")
    println("The number of allowed address is ${blacklist.totalAllowed}")
}


private val List<UIntRange>.firstAllowed: UInt get() {
    var firstAllowed = 0u

    while (true) {
        val inclusiveRange = find { firstAllowed in it } ?: return firstAllowed
        firstAllowed = inclusiveRange.last + 1u
    }
}

private val List<UIntRange>.totalAllowed: UInt get() {
    var allowed = 0u
    var nextAddress = 0u

    forEach { range ->
        if (nextAddress < range.first) allowed += range.first - nextAddress - 1u
        nextAddress = maxOf(nextAddress, range.last)
    }

    return allowed + UInt.MAX_VALUE - nextAddress
}
