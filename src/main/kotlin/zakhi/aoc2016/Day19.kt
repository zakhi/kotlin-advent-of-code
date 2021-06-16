package zakhi.aoc2016

import zakhi.helpers.entireTextOf


fun main() {
    val elves = entireTextOf("aoc2016/day19").trim().toInt()

    val winner = (elves - highestPowerOfTwoUnder(elves)) * 2 + 1
    println("The winner is elf $winner")

    val secondWinner = (1..elves).fold(1) { previousWinner, players ->
        val newWinner = (1 + previousWinner + if (previousWinner >= players / 2) 1 else 0).mod(players)
        if (newWinner == 0) players else newWinner
    }

    println("The second winner is elf $secondWinner")
}


private fun highestPowerOfTwoUnder(number: Int): Int {
    var power = 2
    while (power < number) power *= 2
    return power / 2
}
