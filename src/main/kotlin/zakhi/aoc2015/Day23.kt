package zakhi.aoc2015

import zakhi.helpers.fail
import zakhi.helpers.linesOf
import zakhi.helpers.tryMatch


fun main() {
    val input = linesOf("aoc2015/day23").toList()
    val computer = Computer()

    computer.runProgram(input)
    println("The value in register b is ${computer.b}")

    val otherComputer = Computer("a" to 1, "b" to 0)
    otherComputer.runProgram(input)
    println("The value in register b when a is initialized to 1 is ${otherComputer.b}")
}

private class Computer(
    vararg startRegisters: Pair<String, Int> = arrayOf("a" to 0, "b" to 0)
) {
    private var position: Int = 0
    private val registers = mutableMapOf(*startRegisters)

    val b: Int get() = registers.getValue("b")

    fun runProgram(input: List<String>) {
        position = 0

        while (position < input.size) {
            val instruction = input[position]
            perform(instruction)
        }
    }

    private fun perform(instruction: String) {
        val currentPosition = position

        tryMatch<Unit>(instruction) {
            Regex("""hlf (\w)""") to { (r) -> registers[r] = registers.getValue(r) / 2 }
            Regex("""tpl (\w)""") to { (r) -> registers[r] = registers.getValue(r) * 3 }
            Regex("""inc (\w)""") to { (r) -> registers[r] = registers.getValue(r) + 1 }
            Regex("""jmp ([+-]?\d+)""") to { (offset) -> position += offset.toInt() }
            Regex("""jie (\w), ([+-]?\d+)""") to { (r, offset) -> if (registers.getValue(r) % 2 == 0) position += offset.toInt() }
            Regex("""jio (\w), ([+-]?\d+)""") to { (r, offset) -> if (registers.getValue(r) == 1) position += offset.toInt() }
        } ?: fail("Unknown instruction $instruction")

        if (currentPosition == position) position += 1
    }
}
