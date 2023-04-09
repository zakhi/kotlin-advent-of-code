package zakhi.aoc2021

import zakhi.helpers.entireTextOf
import zakhi.helpers.join
import zakhi.helpers.mapDestructured


fun main() {
    val (template, rulesInput) = entireTextOf("aoc2021/day14").split("\n\n")
    val rules = Regex("""(\w\w) -> (\w)""").findAll(rulesInput).mapDestructured { (input, output) ->
        input to output
    }.toMap()

    val polymer = Polymer(template)

    repeat(10) { polymer.transform(rules) }
    println("The frequency difference after 10 steps is ${polymer.frequencyDifference}")

    repeat(30) { polymer.transform(rules) }
    println("The frequency difference after 40 steps is ${polymer.frequencyDifference}")
}


private class Polymer(template: String) {
    private var pairCounts = template.zipWithNext().groupingBy { it.toList().join() }.eachCount().mapValues { it.value.toLong() }
    private val lastChar = template.last()

    val frequencyDifference: Long get() {
        val frequencies = buildMap {
            pairCounts.forEach { (pair, count) -> increment(pair.first(), count) }
            increment(lastChar, 1L)
        }

        return frequencies.values.max() - frequencies.values.min()
    }

    fun transform(rules: Map<String, String>) {
        pairCounts = buildMap {
            pairCounts.forEach { (pair, count) ->
                val insertion = rules.getValue(pair)
                increment("${pair.first()}$insertion", count)
                increment("$insertion${pair.last()}", count)
            }
        }
    }

    private fun <T> MutableMap<T, Long>.increment(key: T, amount: Long) {
        put(key, getOrDefault(key, 0) + amount)
    }
}
