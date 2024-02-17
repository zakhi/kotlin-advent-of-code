package zakhi.aoc2022

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail
import zakhi.helpers.join


fun main() {
    val packetPairs = entireTextOf("aoc2022/day13").trim().split("\n\n").map { pair ->
        val packets = pair.split("\n")
        parse(packets[0]) to parse(packets[1])
    }

    val rightOrderCount = packetPairs.withIndex().filter { (_, pair) -> isRightOrder(pair.first, pair.second) == true }.sumOf { it.index + 1 }
    println("The number of packets in the right order is $rightOrderCount")

    val firstDivider = listOf(listOf(2))
    val secondDivider = listOf(listOf(6))

    val allPackets = packetPairs.flatMap { it.toList() } + listOf(firstDivider, secondDivider)
    val sortedPackets = allPackets.sortedWith(PacketComparator)

    val decoderKey = (sortedPackets.indexOf(firstDivider) + 1) * (sortedPackets.indexOf(secondDivider) + 1)
    println("The decoder key is $decoderKey")
}


private fun parse(line: String): List<Any> = parseList(line.drop(1).toMutableList())

fun parseList(chars: MutableList<Char>): List<Any> = buildList {
    val digits = mutableListOf<Char>()

    while (chars.isNotEmpty()) {
        when (val char = chars.removeFirst()) {
            '[' -> add(parseList(chars))
            in '0'..'9' -> digits.add(char)
            else -> {
                if (digits.isNotEmpty()) add(digits.join().toInt())
                digits.clear()

                if (char == ']') break
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun isRightOrder(first: Any, second: Any): Boolean? {
    if (first is Int && second is Int) return isRightOrder(first, second)
    if (first is List<*> && second is Int) return isRightOrder(first, listOf(second))
    if (first is Int && second is List<*>) return isRightOrder(listOf(first), second)

    val firstList = first as List<Any>
    val secondList = second as List<Any>

    firstList.zip(secondList).forEach { (a, b) ->
        val result = isRightOrder(a, b)
        if (result != null) return result
    }

    if (firstList.size == secondList.size) return null
    return firstList.size < secondList.size
}

private fun isRightOrder(first: Int, second: Int): Boolean? {
    if (first == second) return null
    return first < second
}

private object PacketComparator : Comparator<List<Any>> {
    override fun compare(o1: List<Any>, o2: List<Any>): Int = when (isRightOrder(o1, o2)) {
        true -> -1
        false -> 1
        else -> fail("Cannot compare $o1 and $o2")
    }
}
