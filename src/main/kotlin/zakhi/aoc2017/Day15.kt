package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf


fun main() {
    val (aStart, bStart) = matchEachLineOf("aoc2017/day15", Regex(""".*?(\d+)$""")) { (value) -> value.toLong() }
    val aFactor = 16807L
    val bFactor = 48271L

    val generatorA = generator(aStart, aFactor)
    val generatorB = generator(bStart, bFactor)

    val count = countMatching(generatorA, generatorB, times = 40_000_000)
    println("The total count is $count")

    val slowGeneratorA = generator(aStart, aFactor, multiplesOf = 4L)
    val slowGeneratorB = generator(bStart, bFactor, multiplesOf = 8L)

    val finalCount = countMatching(slowGeneratorA, slowGeneratorB, times = 5_000_000)
    println("The final count is $finalCount")
}


private typealias Generator = Sequence<Long>

private fun generator(start: Long, factor: Long, multiplesOf: Long? = null): Sequence<Long> = sequence {
    var value = start

    while (true) {
        value = value * factor % 2147483647
        if (multiplesOf == null || value.mod(multiplesOf) == 0L) yield(value)
    }
}

private fun countMatching(a: Generator, b: Generator, times: Int): Int = a.zip(b).take(times).count { (aValue, bValue) ->
    aValue.toShort() == bValue.toShort()
}
