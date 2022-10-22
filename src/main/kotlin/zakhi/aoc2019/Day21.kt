package zakhi.aoc2019

import zakhi.helpers.charList
import zakhi.helpers.entireTextOf
import zakhi.helpers.join


private val input = readProgramFrom(entireTextOf("aoc2019/day21"))

fun main() {
    val walkDamage = runProgram(
        "NOT A J",
        "NOT B T",
        "AND D T",
        "OR T J",
        "NOT C T",
        "AND D T",
        "OR T J",
        "WALK"
    )

    walkDamage?.let { println("Hull damage detected with walk is $it") }

    val runDamage = runProgram(
        "NOT A J",
        "NOT B T",
        "AND D T",
        "OR T J",
        "NOT C T",
        "AND D T",
        "AND H T",
        "OR T J",
        "RUN"
    )

    runDamage?.let { println("Hull damage detected with run is $it") }
}


private fun runProgram(vararg instructions: String): Long? {
    val programInput = instructions.join { "$it\n" }.charList().toMutableList()
    val output = mutableListOf<Long>()
    IntcodeComputer(input, { programInput.removeFirst().code.toLong() }, { output.add(it) }).start()

    if (output.last() != 10L) return output.last()
    output.forEach { print(it.toInt().toChar()) }

    return null
}
