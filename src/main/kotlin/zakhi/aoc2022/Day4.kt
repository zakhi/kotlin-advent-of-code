package zakhi.aoc2022

import zakhi.helpers.matchEachLineOf


fun main() {
    val pairs = matchEachLineOf("aoc2022/day4", Regex("""(\d+)-(\d+),(\d+)-(\d+)""")) { (start1, end1, start2, end2) ->
        start1.toInt()..end1.toInt() to start2.toInt()..end2.toInt()
    }

    val containingPairs = pairs.count { (range1, range2) -> range1.containsAll(range2) || range2.containsAll(range1) }
    println("The number of pairs where one fully contains the other is $containingPairs")

    val overlappingPairs = pairs.count { (range1, range2) -> range1.overlaps(range2) }
    println("The number of pairs where one overlaps the other is $overlappingPairs")
}


private fun IntRange.containsAll(other: IntRange) = first <= other.first && last >= other.last

private fun IntRange.overlaps(other: IntRange) = first <= other.last && other.first <= last