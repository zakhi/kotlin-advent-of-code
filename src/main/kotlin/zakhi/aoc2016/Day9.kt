package zakhi.aoc2016

import zakhi.helpers.entireTextOf

fun main() {
    val compressed = entireTextOf("aoc2016/day9").replace(Regex("""\s+"""), "")

    println("The decompressed length is ${decompressedLength(compressed, full = false)}")
    println("The fully decompressed length is ${decompressedLength(compressed, full = true)}")

}

private fun decompressedLength(compressed: String, full: Boolean = false): Long {
    var position = 0
    var length = 0L

    while (position <= compressed.length) {
        val match = Regex("""\((\d+)x(\d+)\)""").find(compressed ,startIndex = position)

        if (match == null) {
            length += compressed.length - position
            break
        }

        val (chars, times) = match.groupValues.drop(1).map { it.toInt() }
        length += match.range.first - position

        val firstRepeatingIndex = match.range.last + 1
        val repeatSequence = compressed.substring(firstRepeatingIndex, firstRepeatingIndex + chars)
        length += times * (if (full) decompressedLength(repeatSequence) else chars.toLong())
        position = firstRepeatingIndex + chars
    }

    return length
}
