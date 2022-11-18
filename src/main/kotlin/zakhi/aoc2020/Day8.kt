package zakhi.aoc2020

import zakhi.helpers.fail
import zakhi.helpers.matchEachLineOf

fun main() {
    val instructions = matchEachLineOf("aoc2020/day8", Regex("""(\w{3}) ([+-]\d+)""")) { (name, value) ->
        Instruction(name, value.toInt())
    }

    val console = GameConsole(instructions)
    console.run()

    println("The accumulator value before infinite loop is ${console.accumulator}")

    val successfulConsole = instructions.indices.filter { instructions[it].invertible }.asSequence()
        .map { index ->
            val modifiedInstructions = instructions.toMutableList().apply {
                set(index, get(index).invert())
            }
            GameConsole(modifiedInstructions)
        }.first { it.run() }

    println("The accumulator value after termination is ${successfulConsole.accumulator}")
}


private data class Instruction(
    val name: String,
    val value: Int
) {
    val invertible = name in listOf("jmp", "nop")

    fun invert() = copy(name = when (name) {
        "jmp" -> "nop"
        "nop" -> "jmp"
        else -> fail("$this is not invertible")
    })
}

private class GameConsole(private val instructions: List<Instruction>) {
    var accumulator: Int = 0
        private set

    private var instructionIndex = 0
    private val executedInstructions = mutableSetOf<Int>()

    fun run(): Boolean {
        while (instructionIndex !in executedInstructions) {
            if (instructionIndex !in instructions.indices) return true

            executedInstructions.add(instructionIndex)
            val (instruction, value) = instructions[instructionIndex]

            instructionIndex += if (instruction == "jmp") value else 1
            if (instruction == "acc") accumulator += value
        }

        return false
    }
}
