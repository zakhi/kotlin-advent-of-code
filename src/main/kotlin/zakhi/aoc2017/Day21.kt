package zakhi.aoc2017

import zakhi.helpers.isEven
import zakhi.helpers.join
import zakhi.helpers.matchEachLineOf


fun main() {
    val rules = matchEachLineOf("aoc2017/day21", Regex("""([./#]+) => ([./#]+)""")) { (source, target) ->
        source.split("/") to target.split("/")
    }.flatMap { (source, target) ->
        matchingPatternsOf(source).map { it to target }
    }.toMap()

    val startPattern = listOf(".#.", "..#", "###")

    val patternAfter5 = enhance(startPattern, rules, iterations = 5)
    println("The number of pixels which stay on after 5 iterations is ${patternAfter5.onCount}")

    val patternAfter18 = enhance(patternAfter5, rules, iterations = 13)
    println("The number of pixels which stay on after 18 iterations is ${patternAfter18.onCount}")
}


private typealias Pattern = List<String>

private fun enhance(startPattern: Pattern, rules: Map<Pattern, Pattern>, iterations: Int): Pattern =
    (1..iterations).fold(startPattern) { pattern, _ ->
        splitAndJoin(pattern) { rules.getValue(it) }
    }

private fun splitAndJoin(pattern: Pattern, transform: (Pattern) -> Pattern): Pattern {
    val size = if (pattern.size.isEven) 2 else 3

    return pattern.chunked(size).flatMap { lines ->
        (pattern.indices step size).map { i -> transform(lines.map { it.slice(i until i + size) }) }
            .reduce { a, b -> a.zip(b).map { it.first + it.second }  }
    }
}

private fun matchingPatternsOf(pattern: Pattern) = sequence {
    yield(pattern)
    yield(pattern.mirrored())

    val flipped = pattern.reversed()
    yield(flipped)
    yield(flipped.mirrored())

    val transposed = pattern.transposed()
    yield(transposed)
    yield(transposed.mirrored())

    val flippedTransposed = transposed.reversed()
    yield(flippedTransposed)
    yield(flippedTransposed.mirrored())
}

private fun Pattern.mirrored(): Pattern = map { it.reversed() }

private fun Pattern.transposed(): Pattern = indices.map { index ->
    map { it[index] }.join()
}

private val Pattern.onCount: Int get() = sumOf { line -> line.count { it == '#' } }
