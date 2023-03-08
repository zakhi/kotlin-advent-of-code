package zakhi.aoc2020

import zakhi.helpers.*


fun main() {
    val input = entireTextOf("aoc2020/day23").trim().charList().map { it.digitToInt() }
    val game = CupGame(input)
    game.playMoves(100)

    println("The cup labels after 100 moves are ${game.labelsAfterOne.join()}")

    val longGame = CupGame(input + (input.max() + 1 .. 1000000))
    longGame.playMoves(10000000)

    val labelProduct = longGame.labelsAfterOne.take(2).map { it.toLong() }.toList().product()
    println("The result of the long game is $labelProduct")
}


private class CupGame(start: List<Int>) {
    private val cups: List<Cup>
    private val cupOne: Cup
    private var currentCup: Cup

    init {
        cups = start.map { Cup(it) }
        val cupsByLabel = cups.associateBy { it.label }
        cupOne = cupsByLabel.getValue(1)
        currentCup = cups.first()

        cups.onEachIndexed { index, cup ->
            val previousLabel = if (cup.label == 1) cups.size else cup.label - 1
            cup.previousByLabel = cupsByLabel.getValue(previousLabel)
            cup.next = cups.cyclicNextFrom(index)
            cup.previous = cups.cyclicPreviousFrom(index)
        }
    }

    val labelsAfterOne: Sequence<Int> get() = generateSequence(cupOne) { cup ->
        cup.next.takeIf { it.label != 1 }
    }.drop(1).map { it.label }

    fun playMoves(moves: Int) = repeat(moves) {
        val pickUp = generateSequence(currentCup) { it.next }.drop(1).take(3).toList()
        val destination = generateSequence(currentCup) { it.previousByLabel }.drop(1).dropWhile { it in pickUp }.first()

        val cupAfterPickUp = pickUp.last().next
        currentCup.next = cupAfterPickUp
        cupAfterPickUp.previous = currentCup

        val cupAfterDestination = destination.next
        destination.next = pickUp.first()
        pickUp.first().previous = destination

        cupAfterDestination.previous = pickUp.last()
        pickUp.last().next = cupAfterDestination

        currentCup = currentCup.next
    }
}

private class Cup(val label: Int) {
    lateinit var next: Cup
    lateinit var previous: Cup
    lateinit var previousByLabel: Cup
}
