package zakhi.aoc2021

import zakhi.helpers.Point
import zakhi.helpers.linesOf
import zakhi.helpers.x
import zakhi.helpers.y


fun main() {
    val input = linesOf("aoc2021/day25").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '.') null else (x to y) to c }
    }.toMap()

    val totalMoves = countMoves(input)
    println("The first on which there is no move is $totalMoves")
}


private fun countMoves(initialState: Map<Point, Char>): Int {
    val dimensions = initialState.keys.maxOf { it.x } + 1 to initialState.keys.maxOf { it.y } + 1
    var state = initialState
    var steps = 1

    while (true) {
        val stateAfterHorizontalMoves = state.toMutableMap()

        state.filterValues { it == '>' }.forEach { (position, c) ->
            val destination = (position.x + 1) % dimensions.x to position.y

            if (destination !in state) {
                stateAfterHorizontalMoves[destination] = c
                stateAfterHorizontalMoves.remove(position)
            }
        }

        val stateAfterBothMove = stateAfterHorizontalMoves.toMutableMap()

        stateAfterHorizontalMoves.filterValues { it == 'v' }.forEach { (position, c) ->
            val destination = position.x to (position.y + 1) % dimensions.y

            if (destination !in stateAfterHorizontalMoves) {
                stateAfterBothMove[destination] = c
                stateAfterBothMove.remove(position)
            }
        }

        if (stateAfterBothMove == state) return steps

        state = stateAfterBothMove.toMap()
        steps += 1
    }
}
