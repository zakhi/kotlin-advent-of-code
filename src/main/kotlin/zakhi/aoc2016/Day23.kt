package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf

fun main() {
    val instructions = matchEachLineOf("aoc2016/day23", Regex("""(\w+) (-?\w+) ?(-?\w+)?""")) { values ->
        Instruction(values.toList().first(), values.toList().filter { it.isNotEmpty() }.drop(1))
    }

    val computer = Computer()
    computer.registers["a"] = 7
    computer.run(instructions)
    println("The value sent to the safe is ${computer.registers["a"]}")

    val result = (1..12).reduce(Int::times) + 90 * 81
    println("The actual value to sent to the safe if $result")
}
