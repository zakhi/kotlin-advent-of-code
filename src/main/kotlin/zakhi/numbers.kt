package zakhi

import kotlin.math.sqrt


@Suppress("ClassName")
object numbers {

    fun wholeNumbers(from: Int = 1): Sequence<Int> = generateSequence(from) { it + 1 }


    fun Int.divisors(): Sequence<Int> = sequence {
        val number = this@divisors
        yield(1)

        (2..number.floorSqrt()).forEach { i ->
            if (number % i == 0) {
                yield(i)
                if (number / i != i) yield(number / i)
            }
        }

        yield(number)
    }.sorted()


    fun Int.floorSqrt(): Int = sqrt(toDouble()).toInt()

    val Int.isOdd get() = this % 2 != 0

    fun Iterable<Int>.product(): Int = reduce(Math::multiplyExact)
    fun Iterable<Long>.product(): Long = reduce(Math::multiplyExact)
}

