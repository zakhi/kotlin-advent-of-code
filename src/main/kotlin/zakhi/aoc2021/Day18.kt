package zakhi.aoc2021

import zakhi.helpers.linesOf
import zakhi.helpers.orderedPairs


fun main() {
    val numbers = linesOf("aoc2021/day18").map { parseSnailfishNumber(it) }.toList()

    val sum = numbers.reduce { a, b -> a + b }
    println("The magnitude of the final sum is ${sum.magnitude}")

    val largestMagnitude = numbers.orderedPairs().maxOf { (a, b) -> (a + b).magnitude }
    println("The magnitude of the largest sum is $largestMagnitude")
}


private fun parseSnailfishNumber(text: String) = SnailfishNumber(text.mapNotNull { c -> when {
    c == '[' -> NumberStart
    c == ']' -> NumberEnd
    c.isDigit() -> Number(c.digitToInt())
    else -> null
} })

private data class SnailfishNumber(private val tokens: List<Token>) {
    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        val sum = SnailfishNumber(listOf(NumberStart) + tokens + other.tokens + listOf(NumberEnd))
        return sum.reduce()
    }

    val magnitude: Int get() {
        return magnitudeOf(tokens.toMutableList())
    }

    private fun reduce(): SnailfishNumber {
        var current = this

        while (true) {
            val reduced = current.reduceStep()
            if (reduced == current) return current
            current = reduced
        }
    }

    private fun reduceStep(): SnailfishNumber {
        val exploded = explode()
        if (exploded != this) return exploded
        return split()
    }

    private fun explode(): SnailfishNumber {
        val index = tokens.zipWithNext().withIndex().indexOfFirst { (i, pair) ->
            pair.first is Number && pair.second is Number && levelOfIndex(i) > 4
        }

        if (index == -1) return this

        val left = tokens[index] as Number
        val right = tokens[index + 1] as Number
        val modifiedTokens = tokens.toMutableList()

        val indexToAddLeft = modifiedTokens.take(index - 1).indexOfLast { it is Number }

        if (indexToAddLeft != -1) {
            modifiedTokens[indexToAddLeft] = modifiedTokens[indexToAddLeft] as Number + left
        }

        val offsetToAddRight = modifiedTokens.drop(index + 3).indexOfFirst { it is Number }

        if (offsetToAddRight != -1) {
            modifiedTokens[index + 3 + offsetToAddRight] = modifiedTokens[index + 3 + offsetToAddRight] as Number + right
        }

        return SnailfishNumber(modifiedTokens.take(index - 1) + Number(0) + modifiedTokens.drop(index + 3))
    }

    private fun split(): SnailfishNumber {
        val index = tokens.indexOfFirst { it is Number && it.value >= 10 }
        if (index == -1) return this

        val number = tokens[index] as Number
        return SnailfishNumber(this.tokens.take(index) + number.split() + this.tokens.drop(index + 1))
    }

    private fun levelOfIndex(index: Int): Int = tokens.take(index).let { tokensBefore ->
        tokensBefore.count { it is NumberStart } - tokensBefore.count { it is NumberEnd }
    }

    private fun magnitudeOf(tokens: MutableList<Token>): Int = when (val token = tokens.removeLast()) {
        is Number -> token.value
        NumberStart -> magnitudeOf(tokens)
        NumberEnd -> {
            val right = magnitudeOf(tokens)
            val left = magnitudeOf(tokens)
            3 * left + 2 * right
        }
    }
}

private sealed interface Token

private object NumberStart : Token
private object NumberEnd : Token

@JvmInline
private value class Number(val value: Int) : Token {
    operator fun plus(other: Number): Number = Number(value + other.value)

    fun split(): List<Token> = listOf(NumberStart, Number(value / 2), Number(value / 2 + value % 2), NumberEnd)
}
