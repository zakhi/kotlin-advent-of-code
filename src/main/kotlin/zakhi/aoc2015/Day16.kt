package zakhi.aoc2015

import zakhi.input.matchEachLineOf


fun main() {
    val aunts = matchEachLineOf("aoc2015/day16", Regex("""Sue (\d+): (.*)""")) { (index, properties) ->
        Aunt(index.toInt(), properties.split(", ").associate { parseProperty(it) })
    }

    val requirements = mapOf<String, (Int) -> Boolean>(
        "children" to { it == 3 },
        "cats" to { it == 7 },
        "samoyeds" to { it == 2 },
        "pomeranians" to { it == 3 },
        "akitas" to { it == 0 },
        "vizslas" to { it == 0 },
        "goldfish" to { it == 5 },
        "trees" to { it == 3 },
        "cars" to { it == 2 },
        "perfumes" to { it ==  1 }
    )

    val matchingAunt = aunts.first { it.matches(requirements) }
    println("The number of matching aunt is ${matchingAunt.number}")

    val realRequirements = requirements + mapOf(
        "cats" to { it > 7 },
        "trees" to { it > 3 },
        "pomeranians" to { it < 3 },
        "goldfish" to { it < 5 }
    )

    val realMatchingAunt = aunts.first { it.matches(realRequirements) }
    println("The number of the real matching aunt is ${realMatchingAunt.number}")
}


private fun parseProperty(text: String): Pair<String, Int> {
    val (name, count) = Regex("""(\w+): (\d+)""").matchEntire(text)?.destructured ?: throw Exception("property '$text' is invalid")
    return name to count.toInt()
}

private data class Aunt(
    val number: Int,
    private val properties: Map<String, Int>
) {

    fun matches(requirements: Map<String, (Int) -> Boolean>): Boolean = properties.all { (name, count) ->
        requirements.getValue(name).invoke(count)
    }
}
