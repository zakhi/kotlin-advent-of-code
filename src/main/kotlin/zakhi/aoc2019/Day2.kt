package zakhi.aoc2019

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail
import zakhi.helpers.grid


fun main() {
    val program = entireTextOf("aoc2019/day2").trim().split(",").map { it.toInt() }
    val computer = IntCodeComputer(program)

    val result = computer.run(noun = 12, verb = 2)
    println("The value at position 0 is $result")

    val (noun, verb) = grid(0..99).first { (noun, verb) -> computer.run(noun, verb) == 19690720 }
    println("The noun and verb combination is ${100 * noun + verb}")
}

private class IntCodeComputer(private val program: List<Int>) {

    fun run(noun: Int, verb: Int): Int {
        var position = 0
        val values = program.toMutableList()
        values[1] = noun
        values[2] = verb

        while (true) {
            val (command, a ,b, c) = values.slice(position .. position + 3)
            when (command) {
                99 -> break
                1 -> values[c] = values[a] + values[b]
                2 -> values[c] = values[a] * values[b]
                else -> fail("Invalid opcode at position $position: $command")
            }

            position += 4
        }

        return values[0]
    }
}
