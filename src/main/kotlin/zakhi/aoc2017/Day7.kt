package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf


fun main() {
    val programs = matchEachLineOf("aoc2017/day7", Regex("""(\w+) \((\d+)\)(?: -> ([\w, ]+))?""")) { (name, weight, holding) ->
        Program(name, weight.toInt(), holding.split(", ").filter { it.isNotEmpty() })
    }

    val tower = ProgramTower(programs)
    println("The bottom program is ${tower.findBottomProgram().name}")
    println("The balancing weight is ${tower.findBalancingWeight()}")
}


private class ProgramTower(allPrograms: List<Program>) {
    private val programs = allPrograms.associateBy { it.name }
    private val totalWeights = mutableMapOf<Program, Int>()

    private val root by lazy {
        val dependencies = programs.flatMap { (_, program) -> program.holding.map { it to program.name } }.toMap()
        programs.values.first { it.name !in dependencies }
    }

    fun findBottomProgram(): Program = root

    fun findBalancingWeight(): Int {
        val (program, weightDiff) = findOddProgramDirectlyAbove(root) ?: throw Exception("No balancing required")
        return weightDiff + findProgramToBalanceAbove(program).weight
    }

    private fun findOddProgramDirectlyAbove(program: Program): Pair<Program, Int>? {
        val weightCounts = program.programsAbove.groupingBy { it.totalWeight }.eachCount()
        val oddWeight = weightCounts.entries.find { it.value == 1 }?.key ?: return null
        val commonWeight = weightCounts.entries.first { it.value > 1 }.key

        return program.programsAbove.first { it.totalWeight == oddWeight } to commonWeight - oddWeight
    }

    private fun findProgramToBalanceAbove(program: Program): Program {
        val (heldProgram, _) = findOddProgramDirectlyAbove(program) ?: return program
        return findProgramToBalanceAbove(heldProgram)
    }

    private val Program.totalWeight: Int get() = totalWeights.getOrPut(this) {
        weight + programsAbove.sumOf { it.totalWeight }
    }

    private val Program.programsAbove: List<Program> get() = holding.map { programs.getValue(it) }
}

private data class Program(
    val name: String,
    val weight: Int,
    val holding: List<String>
)
