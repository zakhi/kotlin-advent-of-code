package zakhi.aoc2015

import zakhi.helpers.entireTextOf
import zakhi.helpers.join


fun main() {
    val initialSequence = entireTextOf("aoc2015/day10")

    val resultAfter40 = generateSequences(initialSequence, times = 40)
    println("The length of the result after 40 times is ${resultAfter40.length}")

    val resultAfter50 = generateSequences(resultAfter40, times = 10)
    println("The length of the result after 50 times is ${resultAfter50.length}")
}


private fun generateSequences(initialSequence: String, times: Int) =
    (1..times).fold(initialSequence) { currentSequence, _ ->
        Regex("""(\d)\1*""").findAll(currentSequence).join { "${it.value.length}${it.groupValues[1]}" }
    }
