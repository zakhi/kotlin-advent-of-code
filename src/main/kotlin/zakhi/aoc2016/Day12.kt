package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf


fun main() {
    val instructions = matchEachLineOf("aoc2016/day12", Regex("""(\w+) (\w+) ?(-?\w+)?""")) { values ->
        Instruction(values.toList().first(), values.toList().filter { it.isNotEmpty() }.drop(1))
    }

    val computer = Computer()
    computer.run(instructions)
    println("The value of register a is ${computer.registers["a"]}")

    val actualComputer = Computer()
    actualComputer.registers["c"] = 1
    actualComputer.run(instructions)
    println("The actual value of register a is ${actualComputer.registers["a"]}")
}
