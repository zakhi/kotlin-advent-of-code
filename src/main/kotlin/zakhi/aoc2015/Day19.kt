package zakhi.aoc2015

import zakhi.linesOf


fun main() {
    val lines = linesOf("aoc2015/day19").toList()

    val targetMolecule = lines.first { Regex("""\w+""").matches(it) }
    val rules = lines.mapNotNull {
        Regex("""(\w+) => (\w+)""").matchEntire(it)?.destructured?.let { (source, target) -> source to target }
    }

    val newMolecules = rules.flatMap { (source, target) ->
        Regex("""\Q$source\E""").findAll(targetMolecule).map { targetMolecule.replaceRange(it.range, target) }
    }.distinct()

    println("There are ${newMolecules.count()} different molecules after a single replacement")

    val elements = Regex("[A-Z][a-z]?").findAll(targetMolecule).map { it.value }.toList()
    val steps = elements.size - 1 - elements.count { it == "Rn" || it == "Ar" } - 2 * elements.count { it == "Y" }

    println("The number of steps required to produce the medicine is $steps")
}
