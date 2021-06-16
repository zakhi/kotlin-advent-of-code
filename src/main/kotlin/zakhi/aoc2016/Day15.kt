package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.wholeNumbers


fun main() {
    val discs = matchEachLineOf("aoc2016/day15", Regex("""Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+)\.""")) { (ordinal, positions, initialPosition) ->
        RotatingDisc(ordinal.toInt(), positions.toInt(), initialPosition.toInt())
    }

    val pressTime = wholeNumbers(from = 0).first { time -> discs.all { it.atSlotPosition(time) } }
    println("The first time to press the button is $pressTime")

    val newDiscs = discs + RotatingDisc(ordinal = discs.size + 1, positions = 11, initialPosition = 0)
    val newPressTime = wholeNumbers(from = 0).first { time -> newDiscs.all { it.atSlotPosition(time) } }
    println("The first time to press the button with the additional disc is $newPressTime")
}


private class RotatingDisc(
    private val ordinal: Int,
    private val positions: Int,
    private val initialPosition: Int
) {
    fun atSlotPosition(time: Int): Boolean = (ordinal + initialPosition + time).mod(positions) == 0
}
