package zakhi.aoc2019

import zakhi.helpers.*
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO


fun main() {
    val instructions = linesOf("aoc2019/day22").toList()

    val smallDeck = Deck(size = 10007L, instructions)
    val cardPosition = smallDeck.newIndexOf(2019L)
    println("The final position of 2019 is $cardPosition")

    val largeDeck = Deck(size = 119315717514047, instructions)
    val originalCard = largeDeck.oldIndexOf(2020L, shuffles = 101741582076661)
    println("Card ending on 2020 is $originalCard")
}


private data class LinearCongruentFunction(
    val a: BigInteger,
    val b: BigInteger,
    val m: BigInteger
) {
    companion object {
        fun identity(modulo: BigInteger) = LinearCongruentFunction(ONE, ZERO, modulo)
    }

    operator fun invoke(x: BigInteger): BigInteger = (a * x + b).mod(m)

    fun compose(other: LinearCongruentFunction): LinearCongruentFunction {
        if (m != other.m) throw Exception("Cannot compose functions with different mod")
        return copy(a = (a * other.a).mod(m), b = (b * other.a + other.b).mod(m))
    }

    fun inverse(): LinearCongruentFunction {
        val aInverse = a.modInverse(m)
        return copy(a = aInverse, b = (-b * aInverse).mod(m))
    }

    fun pow(k: BigInteger): LinearCongruentFunction {
        val aToK = a.modPow(k, m)
        return copy(a = aToK, b = (b * (ONE - aToK) * (ONE - a).modInverse(m)).mod(m))
    }
}

private class Deck(
    size: Long,
    instructions: List<String>
) {
    private val deckSize = size.toBigInteger()

    private val lcf: LinearCongruentFunction = instructions.fold(LinearCongruentFunction.identity(deckSize)) { current, instruction ->
        current.compose(tryMatch<LinearCongruentFunction>(instruction) {
            Regex("""deal into new stack""") to { LinearCongruentFunction(-ONE, -ONE, deckSize) }
            Regex("""cut (-?\d+)""") to { (n) -> LinearCongruentFunction(ONE, -BigInteger(n), deckSize) }
            Regex("""deal with increment (\d+)""") to { (n) -> LinearCongruentFunction(BigInteger(n), ZERO, deckSize) }
        } ?: throw Exception("Invalid instruction $instruction"))
    }

    fun newIndexOf(position: Long, shuffles: Long = 1L): Long =
        lcf.pow(shuffles.toBigInteger())(position.toBigInteger()).toLong()

    fun oldIndexOf(position: Long, shuffles: Long = 1): Long =
        lcf.pow(shuffles.toBigInteger()).inverse()(position.toBigInteger()).toLong()
}
