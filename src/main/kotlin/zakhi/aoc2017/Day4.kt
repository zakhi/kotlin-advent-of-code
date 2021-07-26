package zakhi.aoc2017

import zakhi.helpers.linesOf
import kotlin.streams.toList


fun main() {
    val phrases = linesOf("aoc2017/day4").map { it.split(" ") }

    val valid = phrases.count { it.size == it.distinct().size }
    println("The number of valid passphrases is $valid")

    val newValid = phrases.count {
        val sortedWords = it.map { word -> word.chars().sorted().toList() }
        sortedWords.size == sortedWords.distinct().size
    }

    println("The number of new valid passphrases is $newValid")
}
