package zakhi.aoc2016

import zakhi.helpers.linesOf
import zakhi.helpers.join


fun main() {
    val instructions = linesOf("aoc2016/day2").toList()

    val code = KeyPad().readCode(instructions)
    println("The bathroom code is $code")

    val complexCode = ComplexKeyPad().readCode(instructions)
    println("The actual bathroom code is $complexCode")
}


private class KeyPad {
    private var position = 5

    fun readCode(instructions: List<String>): String = instructions.join { follow(it) }

    private fun follow(digitInstructions: String): String {
        digitInstructions.forEach { move ->
            position += when (move) {
                'U' -> if (position in 1..3) 0 else -3
                'D' -> if (position in 7..9) 0 else 3
                'L' -> if (position in listOf(1, 4, 7)) 0 else -1
                'R' -> if (position in listOf(3, 6, 9)) 0 else 1
                else -> throw Exception("Invalid move $move")
            }
        }

        return position.toString()
    }
}

private class ComplexKeyPad {
    private var position = "5"

    private val upMoves = mapOf("3" to "1", "6" to "2", "7" to "3", "8" to "4", "A" to "6", "B" to "7", "C" to "8", "D" to "B")
    private val downMoves = upMoves.map { it.value to it.key }.toMap()
    private val leftMoves = mapOf("6" to "5", "3" to "2", "7" to "6", "B" to "A", "4" to "3", "8" to "7", "C" to "B", "9" to "8")
    private val rightMoves = leftMoves.map { it.value to it.key }.toMap()

    fun readCode(instructions: List<String>): String = instructions.join { follow(it) }

    private fun follow(digitInstructions: String): String {
        digitInstructions.forEach { move ->
            position = when (move) {
                'U' -> upMoves[position]
                'D' -> downMoves[position]
                'L' -> leftMoves[position]
                'R' -> rightMoves[position]
                else -> throw Exception("invalid move $move")
            } ?: position
        }

        return position
    }
}
