package zakhi.aoc2017

import zakhi.helpers.entireTextOf
import zakhi.helpers.join
import zakhi.helpers.tryMatch


fun main() {
    val moves = entireTextOf("aoc2017/day16").trim().split(",")
    val programs = ('a'..'p').join()

    val order = performDance(moves, programs)
    println("The order after a dance is $order")

    val (firstOccurrence, secondOccurrence) = findRepeatingOrder(order, moves)
    val repeat = (1_000_000_000 - firstOccurrence + 1) % (secondOccurrence - firstOccurrence)

    val finalOrder = (1..repeat).fold(programs) { currentOrder, _ -> performDance(moves, currentOrder) }
    println("The final order is $finalOrder")
}


private fun performDance(moves: List<String>, positions: String): String =
    moves.fold(positions) { position, move ->
        tryMatch(move) {
            Regex("""s(\d+)""") to { (x) -> position.takeLast(x.toInt()) + position.dropLast(x.toInt()) }
            Regex("""x(\w+)/(\w+)""") to { (a, b) -> position.swap(a.toInt(), b.toInt()) }
            Regex("""p(\w)/(\w)""") to { (a, b) -> position.swap(position.indexOf(a), position.indexOf(b)) }
        } ?: throw Exception("Invalid move $move")
    }

private fun findRepeatingOrder(initialOrder: String, moves: List<String>): Pair<Int, Int> {
    var order = initialOrder
    val orderOccurrences = mutableMapOf(order to 1)

    for (time in 2..1_000_000_000) {
        order = performDance(moves, order)

        if (order in orderOccurrences) return orderOccurrences.getValue(order) to time
        orderOccurrences[order] = time
    }

    throw Exception("no repeating order found")
}

private fun String.swap(firstIndex: Int, secondIndex: Int): String {
    val array = toCharArray()

    val firstValue = array[firstIndex]
    array[firstIndex] = array[secondIndex]
    array[secondIndex] = firstValue

    return String(array)
}
