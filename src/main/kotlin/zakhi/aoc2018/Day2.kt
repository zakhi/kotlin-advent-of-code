package zakhi.aoc2018

import zakhi.helpers.join
import zakhi.helpers.linesOf
import zakhi.helpers.pairs
import kotlin.streams.toList


fun main() {
    val ids = linesOf("aoc2018/day2").map { BoxId(it) }.toList()
    val checksum = ids.count { it.containsPair } * ids.count { it.containsTriplet }
    println("The checksum is $checksum")

    val idSize = ids.first().length
    val commonLetters = ids.pairs().map { (a, b) -> a.commonLettersWith(b) }.first { it.length == idSize - 1 }
    println("The common letters are $commonLetters")
}


private class BoxId(id: String) {
    private val chars = id.toCharArray()
    private val counts = id.chars().toList().groupingBy { it }.eachCount()

    val containsPair: Boolean get() = 2 in counts.values
    val containsTriplet: Boolean get() = 3 in counts.values
    val length = chars.size

    fun commonLettersWith(other: BoxId): String =
        chars.zip(other.chars).filter { (a, b) -> a == b }.join { it.first.toString() }
}
