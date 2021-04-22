package zakhi.aoc2015

import zakhi.cyclicNext
import zakhi.cyclicPrevious
import zakhi.matchEachLineOf
import zakhi.permutations


fun main() {
    val people = happinessRules.keys.flatMap { it.toList() }.distinct()
    val happinessChange = allArrangementsOf(people).maxOf { totalHappinessChange(it) }
    println("Total happiness change in optimal arrangement is $happinessChange")

    val peopleWithMyself = people + "Me"
    val happinessChangeWithMyself = allArrangementsOf(peopleWithMyself).maxOf { totalHappinessChange(it) }
    println("Total happiness change in optimal arrangement with myself is $happinessChangeWithMyself")
}


private val happinessRules = matchEachLineOf("aoc2015/day13", Regex("""(\w+) would (gain|lose) (\d+) happiness units by sitting next to (\w+)\.""")) {
    (source, gainOrLose, change, target) -> (source to target) to change.toInt() * if (gainOrLose == "gain") 1 else -1
}.toMap().withDefault { 0 }


private fun allArrangementsOf(people: List<String>): List<List<String>> {
    return people.drop(1).permutations().map { it + people.first() }
}

private fun totalHappinessChange(arrangement: List<String>): Int = arrangement.indices.sumBy { index ->
    val person = arrangement[index]
    val nextPerson = arrangement.cyclicNext(index)
    val previousPerson = arrangement.cyclicPrevious(index)

    happinessRules.getValue(person to nextPerson) + happinessRules.getValue(person to previousPerson)
}
