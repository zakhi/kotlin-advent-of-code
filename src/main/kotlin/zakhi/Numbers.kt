package zakhi

import java.lang.Math.multiplyExact
import kotlin.math.sqrt


fun naturalNumbers(from: Int = 1): Sequence<Int> = generateSequence(from) { it + 1 }


fun Int.divisors(): Sequence<Int> = sequence {
    val number = this@divisors
    yield(1)

    (2..number.sqrtFloor()).forEach { i ->
        if (number % i == 0) {
            yield(i)
            if (number / i != i) yield(number / i)
        }
    }

    yield(number)
}.sorted()


fun Int.sqrtFloor(): Int = sqrt(toDouble()).toInt()

val Int.isOdd get() = this % 2 != 0

fun Int.primeFactors(): Map<Int, Int> = sequence {
    var current = this@primeFactors

    while (current % 2 == 0) {
        yield(2)
        current /= 2
    }

    generateSequence(3) { it + 2 }.takeWhile { it <= current.sqrtFloor() }.forEach { factor ->
        while (current % factor == 0) {
            yield(factor)
            current /= factor
        }
    }

    if (current > 2) yield(current)
}.groupingBy { it }.eachCount()


fun Iterable<Int>.product(): Int = reduce(Math::multiplyExact)
fun Iterable<Long>.product(): Long = reduce(Math::multiplyExact)
