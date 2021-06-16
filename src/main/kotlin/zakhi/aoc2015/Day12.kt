package zakhi.aoc2015

import kotlinx.serialization.json.*
import zakhi.helpers.entireTextOf


fun main() {
    val json = Json.parseToJsonElement(entireTextOf("aoc2015/day12"))

    val numbers = allNumbersInside(json)
    println("The sum of all numbers is ${numbers.sum()}")

    val numbersFiltered = allNumbersInside(json, filtered = true)
    println("The sum of all numbers after filtering is ${numbersFiltered.sum()}")
}

private fun allNumbersInside(json: JsonElement, filtered: Boolean = false): List<Int> = when (json) {
    is JsonPrimitive -> json.intOrNull?.let { listOf(it) } ?: emptyList()
    is JsonArray -> json.flatMap { allNumbersInside(it, filtered) }
    is JsonObject -> if (filtered && json.containsValue(JsonPrimitive("red"))) emptyList() else json.values.flatMap { allNumbersInside(it, filtered) }
}
