package zakhi.aoc2018

import zakhi.helpers.entireTextOf
import kotlin.text.RegexOption.IGNORE_CASE


fun main() {
    val initialPolymer = entireTextOf("aoc2018/day5").trim()
    val reactedPolymer = react(initialPolymer)

    println("The length of the reacted polymer is ${reactedPolymer.length}")

    val shortestPolymerLength = ('a'..'z').minOf {
        val polymer = initialPolymer.replace(Regex(it.toString(), IGNORE_CASE), "")
        react(polymer).length
    }

    println("The shortest polymer's length is $shortestPolymerLength")
}


private fun react(polymer: String): String = buildString {
    polymer.toCharArray().forEach { char ->
        if (isEmpty() || !char.reactsWith(last())) {
            append(char)
        } else {
            deleteCharAt(lastIndex)
        }
    }
}

private fun Char.reactsWith(other: Char): Boolean = this != other && this.uppercase() == other.uppercase()
