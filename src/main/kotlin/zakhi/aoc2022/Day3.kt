package zakhi.aoc2022

import zakhi.helpers.linesOf


fun main() {
    val rucksacks = linesOf("aoc2022/day3").toList()

    val prioritySum = rucksacks.map { repeatingItem(it) }.sumOf { itemPriority(it) }
    println("The priority sum is $prioritySum")

    val groups = rucksacks.chunked(3)
    val badgesPrioritySum = groups.map { commonItem(it) }.sumOf { itemPriority(it) }
    println("The badges priority sum is $badgesPrioritySum")
}


private fun repeatingItem(rucksack: String): Char =
    rucksack.chunked(rucksack.length / 2).map { it.toSet() }.reduce { a, b -> a intersect b }.first()

private fun commonItem(rucksacks: List<String>): Char =
    rucksacks.map { it.toSet() }.reduce { a, b -> a intersect b }.first()

private val priorities = CharRange('a', 'z').toList() + CharRange('A', 'Z').toList()

private fun itemPriority(item: Char) = priorities.indexOf(item) + 1
