package zakhi.aoc2016

import zakhi.helpers.linesOf
import zakhi.helpers.join


fun main() {
    val messages = linesOf("aoc2016/day6").toList()
    val letterFrequencies = messages.first().indices.map { index ->
        messages.map { it[index] }.groupingBy { it }.eachCount()
    }

    val sentMessage = letterFrequencies.join { it.mostCommonChar() }
    println("The message is $sentMessage")

    val actualSentMessage = letterFrequencies.join { it.leastCommonChar() }
    println("The actual message is $actualSentMessage")
}

private fun Map<Char, Int>.mostCommonChar() = maxByOrNull { it.value }?.key.toString()
private fun Map<Char, Int>.leastCommonChar() = minByOrNull { it.value }?.key.toString()
