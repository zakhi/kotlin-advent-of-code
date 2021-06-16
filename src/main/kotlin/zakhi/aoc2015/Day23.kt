package zakhi.aoc2015

import zakhi.helpers.linesOf


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

        for ((pattern, operation) in instructionPatterns) {
            val match = pattern.matchEntire(instruction)

            if (match != null) {
                operation(match.destructured)
                if (currentPosition == position) position += 1
                return
            }
        }

        throw Exception("Unknown instruction $instruction")
    }

    private val instructionPatterns = mapOf<Regex, (MatchResult.Destructured) -> Unit>(
        Regex("""hlf (\w)""") to { (r: String) -> registers[r] = registers.getValue(r) / 2 },
        Regex("""tpl (\w)""") to { (r: String) -> registers[r] = registers.getValue(r) * 3 },
        Regex("""inc (\w)""") to { (r: String) -> registers[r] = registers.getValue(r) + 1 },
        Regex("""jmp ([+-]?\d+)""") to { (offset: String) -> position += offset.toInt() },
        Regex("""jie (\w), ([+-]?\d+)""") to { (r: String, offset: String) -> if (registers.getValue(r) % 2 == 0) position += offset.toInt() },
        Regex("""jio (\w), ([+-]?\d+)""") to { (r: String, offset: String) -> if (registers.getValue(r) == 1) position += offset.toInt() },
    )
}
