package zakhi.aoc2016

import zakhi.collections.second
import zakhi.input


fun main() {
    val instructions = input.matchEachLineOf("aoc2016/day12", Regex("""(\w+) (\w+) ?(-?\w+)?""")) { values ->
        Instruction(values.toList().first(), values.toList().filter { it.isNotEmpty() }.drop(1))
    }

    val computer = Computer()
    computer.run(instructions)
    println("The value of register a is ${computer.a}")

    val actualComputer = Computer("c" to 1)
    actualComputer.run(instructions)
    println("The actual value of register a is ${actualComputer.a}")
}


private class Computer(vararg registerValues: Pair<String, Int> = emptyArray()) {
    private val registers = registerValues.toMap().toMutableMap().withDefault { 0 }
    private var pointer = 0

    val a: Int get() = registers.getValue("a")

    fun run(instructions: List<Instruction>) {
        while (pointer in instructions.indices) {
            val (code, arguments) = instructions[pointer]
            when (code) {
                "cpy" -> copy(arguments.first(), arguments.second())
                "inc" -> update(arguments.first(), 1)
                "dec" -> update(arguments.first(), -1)
                "jnz" -> jump(arguments.first(), arguments.second().toInt())
                else -> throw Exception("Unknown instruction $code")
            }
        }
    }

    private fun copy(source: String, target: String) {
        registers[target] = registerValueOrInt(source)
        pointer += 1
    }

    private fun update(target: String, amount: Int) {
        registers[target] = registers.getValue(target) + amount
        pointer += 1
    }

    private fun jump(source: String, amount: Int) {
        val value = registerValueOrInt(source)
        pointer += if (value != 0) amount else 1
    }

    private fun registerValueOrInt(source: String) = source.toIntOrNull() ?: registers.getValue(source)
}

private data class Instruction(
    val code: String,
    val arguments: List<String>
)
