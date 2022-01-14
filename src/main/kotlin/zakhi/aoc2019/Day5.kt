package zakhi.aoc2019

import zakhi.helpers.entireTextOf


fun main() {
    val program = entireTextOf("aoc2019/day5").trim().split(",").map { it.toInt() }
    val computer = IntCodeComputerV2(program)
    val output = computer.run(listOf(1))

    val diagnosticCode = output.last()
    println("The diagnostic code for system ID 1 is $diagnosticCode")

    val anotherComputer = IntCodeComputerV2(program)
    val anotherOutput = anotherComputer.run(listOf(5))

    val anotherDiagnosticCode = anotherOutput.last()
    println("The diagnostic code for system ID 5 is $anotherDiagnosticCode")
}


private class IntCodeComputerV2(program: List<Int>) {

    private val program = program.toMutableList()
    private var position = 0

    fun run(input: List<Int>): List<Int> {
        val inputs = input.toMutableList()
        val outputs = mutableListOf<Int>()
        position = 0

        while (true) {
            val command = nextCommand()
            val originalPosition = position

            when (command[0]) {
                99 -> break
                1 -> command[3] = command[1] + command[2]
                2 -> command[3] = command[1] * command[2]
                3 -> command[1] = inputs.removeFirst()
                4 -> outputs.add(command[1])
                5 -> if (command[1] != 0) position = command[2]
                6 -> if (command[1] == 0) position = command[2]
                7 -> command[3] = if (command[1] < command[2]) 1 else 0
                8 -> command[3] = if (command[1] == command[2]) 1 else 0
            }

            if (position == originalPosition) {
                position += command.size
            }
        }

        return outputs
    }

    private fun nextCommand() = IntCodeCommand()

    private inner class IntCodeCommand {
        private val code get() = program[position]
        private val arguments get() = program.subList(position, position + size)

        val size get() = when (this[0]) {
            in 3..4 -> 2
            in 5..6 -> 3
            else -> 4
        }

        operator fun get(index: Int): Int {
            if (index == 0) return code.mod(100)

            val argument = arguments[index]
            val positionCode = (code / 100).toString().reversed().getOrNull(index - 1)?.digitToInt() ?: 0
            return if (positionCode == 1) argument else program[argument]
        }

        operator fun set(index: Int, value: Int) {
            program[arguments[index]] = value
        }
    }
}
