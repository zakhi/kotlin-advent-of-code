package zakhi.aoc2018

import zakhi.helpers.matchEntireTextOf
import zakhi.helpers.maxBy


fun main() {
    val (players, lastMarble) = matchEntireTextOf("aoc2018/day9", Regex("""(\d+) players; last marble is worth (\d+) points""")) { (players, points) ->
        players.toInt() to points.toLong()
    }

    val game = MarbleGame(players)
    game.playUntil(lastMarble)
    println("The highest score is ${game.highestScore}")

    val longGame = MarbleGame(players)
    longGame.playUntil(lastMarble * 100)
    println("The highest score for the long game is ${longGame.highestScore}")
}


private class MarbleGame(players: Int) {
    private val scores = (0 until players).associateWith { 0L }.toMutableMap()

    val highestScore: Long get() = scores.values.maxBy { it }

    fun playUntil(lastMarble: Long) {
        var currentMarble = Marble(0)

        (1..lastMarble).forEach { turn ->
            val marble = Marble(turn)
            val player = turn.mod(scores.size)

            if (marble.value.mod(23) == 0) {
                currentMarble -= 6
                scores[player] = scores.getValue(player) + marble.value + (currentMarble - 1).remove().value
            } else {
                currentMarble = marble.insertBefore(currentMarble + 2)
            }
        }
    }
}

private class Marble(val value: Long) {
    private var next: Marble = this
    private var previous: Marble = this

    operator fun plus(offset: Long): Marble = (1..offset).fold(this) { current, _ -> current.next }
    operator fun minus(offset: Long): Marble = (1..offset).fold(this) { current, _ -> current.previous }

    fun insertBefore(other: Marble): Marble {
        next = other
        previous = other.previous
        other.previous.next = this
        other.previous = this

        return this
    }

    fun remove(): Marble {
        next.previous = previous
        previous.next = next
        next = this
        previous = this

        return this
    }
}
