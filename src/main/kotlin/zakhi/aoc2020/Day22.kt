package zakhi.aoc2020

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail


fun main() {
    val (firstDeck, secondDeck) = entireTextOf("aoc2020/day22").trim().split("\n\n").map { parseDeck(it.lines()) }

    val game = CombatGame(firstDeck, secondDeck)
    game.play()
    println("The winning score is ${game.winningScore}")

    val recursiveGame = CombatGame(firstDeck, secondDeck, recursive = true)
    recursiveGame.play()
    println("The winning score of the recursive game is ${recursiveGame.winningScore}")
}


private fun parseDeck(lines: List<String>): List<Int> = lines.drop(1).map { it.toInt() }

private class CombatGame(
    firstDeck: List<Int>,
    secondDeck: List<Int>,
    private val recursive: Boolean = false
) {
    private val first = CombatPlayer(firstDeck)
    private val second = CombatPlayer(secondDeck)
    private val previousRoundCards = mutableSetOf<Int>()

    val winningScore: Int get() = winner?.score ?: fail("No winner is declared")

    fun play(): Boolean {
        while (winner == null) {
            previousRoundCards.add(roundCards)

            val firstCard = first.draw()
            val secondCard = second.draw()

            when (determineWinner(firstCard, secondCard)) {
                first -> first.receive(firstCard, secondCard)
                second -> second.receive(secondCard, firstCard)
            }
        }

        return winner == first
    }

    private val winner: CombatPlayer? get() {
        if (recursive && roundCards in previousRoundCards) return first
        return if (first.lost) second else if (second.lost) first else null
    }

    private fun determineWinner(firstCard: Int, secondCard: Int): CombatPlayer {
        if (recursive && first.hasEnoughCards(firstCard) && second.hasEnoughCards(secondCard)) {
            val innerGame = CombatGame(first.take(firstCard), second.take(secondCard), recursive = true)
            return if (innerGame.play()) first else second
        }

        return if (firstCard > secondCard) first else second
    }

    private val roundCards get() = listOf(first.cardsHashcode, second.cardsHashcode).hashCode()
}

private class CombatPlayer(startingDeck: List<Int>) {
    private val deck = startingDeck.toMutableList()

    val lost get() = deck.isEmpty()
    val score get() = deck.reversed().mapIndexed { index, card -> card * (index + 1) }.sum()

    fun draw(): Int = deck.removeFirst()
    fun receive(vararg cards: Int) = deck.addAll(cards.toList())
    fun take(count: Int) = deck.take(count)

    fun hasEnoughCards(count: Int) = deck.size >= count

    val cardsHashcode: Int get() = deck.hashCode()
}
