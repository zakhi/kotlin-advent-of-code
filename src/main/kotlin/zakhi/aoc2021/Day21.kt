package zakhi.aoc2021

import zakhi.helpers.flip
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.pairs


fun main() {
    val players = matchEachLineOf("aoc2021/day21", Regex("""Player . starting position: (\d)""")) { (position) -> Player(position.toInt()) }.pairs().first()

    val game = FullDiceGame(players)
    game.play()

    println("The result of the game is ${game.losingScore * game.diceRolls}")

    val (firstWins, _) = winCountWithDiracDice(players)
    println("The number of wins for the first player is $firstWins")
}

private class FullDiceGame(
    private var players: Pair<Player, Player>
) {
    private val dice = generateSequence(1) { it % 100 + 1 }.iterator()

    val losingScore get() = players.toList().minOf { it.score }

    var diceRolls = 0
        private set

    fun play() {
        while (!gameWon()) {
            val roll = (1..3).sumOf { dice.next() }
            diceRolls += 3
            players = playTurn(players, roll)
        }
    }

    private fun gameWon() = players.toList().maxOf { it.score } >= 1000
}

private data class Player(val position: Int, val score: Int = 0) {
    fun advance(roll: Int): Player {
        var newPosition = (position + roll) % 10
        newPosition = if (newPosition == 0) 10 else newPosition

        return Player(position = newPosition, score = score + newPosition)
    }
}

private fun playTurn(players: Pair<Player, Player>, roll: Int): Pair<Player, Player> = players.second to players.first.advance(roll)

private val rollCounts = mapOf(
    3 to 1,
    4 to 3,
    5 to 6,
    6 to 7,
    7 to 6,
    8 to 3,
    9 to 1
)

private val cache = mutableMapOf<Pair<Player, Player>, Pair<Long, Long>>()

private fun winCountWithDiracDice(players: Pair<Player, Player>): Pair<Long, Long> {
    return cache.getOrPut(players.first to players.second) {
        if (players.first.score >= 21) return 1L to 0L
        if (players.second.score >= 21) return 0L to 1L

        return rollCounts.map { (roll, count) ->
            winCountWithDiracDice(playTurn(players, roll)) * count.toLong()
        }.reduce { a, b -> a + b }.flip()
    }
}

operator fun Pair<Long, Long>.plus(other: Pair<Long, Long>) = first + other.first to second + other.second
operator fun Pair<Long, Long>.times(count: Long) = first * count to second * count
