package zakhi.aoc2019

import zakhi.helpers.charList
import zakhi.helpers.entireTextOf
import zakhi.helpers.join


private val input = entireTextOf("aoc2019/day21").trim().split(",").map { it.toInt() }

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


private fun runProgram(vararg instructions: String): Int? {
    val programInput = instructions.join { "$it\n" }.charList().toMutableList()
    val output = mutableListOf<Int>()
    IntCodeComputer(input, { programInput.removeFirst().code }, { output.add(it) }).start()

    if (output.last() != 10) return output.last()
    output.forEach { print(it.toChar()) }

    return null
}
