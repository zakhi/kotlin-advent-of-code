package zakhi.aoc2020

import zakhi.helpers.matchEachLineOf


fun main() {
    val rules = matchEachLineOf("aoc2020/day7", Regex("""([\w ]+) bags contain (.+)\.""")) { (containerColor, bags) ->
        containerColor to bags.split(", ").mapNotNull {
            Regex("""(\d+) ([\w ]+) bags?""").matchEntire(it)?.destructured?.let { (number, color) ->
                color to number.toInt()
            }
        }.toMap()
    }.toMap()

    val containersOfShinyGold = findContainersOf("shiny gold", rules)
    println("The number of bag colors that can contain shiny gold is ${containersOfShinyGold.size}")

    val bagsInShinyGold = countBagsInside("shiny gold", rules)
    println("The number of bags inside a shiny gold bag is $bagsInShinyGold")
}


private fun findContainersOf(color: String, rules: Map<String, Map<String, Int>>): Set<String> {
    val containersOf = rules.entries.flatMap { (containerColor, bags) -> bags.map { it.key to containerColor } }
        .groupBy { it.first }.mapValues { (_, bags) -> bags.map { it.second }.toSet() }

    val containers = containersOf[color].orEmpty().toMutableSet()
    while (containers.addAll(containers.flatMap { containersOf[it].orEmpty() })) {
        // nothing to do
    }

    return containers
}

private fun countBagsInside(color: String, rules: Map<String, Map<String, Int>>): Int =
    rules[color].orEmpty().entries.sumOf { (bagColor, amount) ->
        amount * (countBagsInside(bagColor, rules) + 1)
    }
