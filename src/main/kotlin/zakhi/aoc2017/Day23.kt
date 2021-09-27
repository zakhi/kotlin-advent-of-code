package zakhi.aoc2017

import zakhi.helpers.divisors
import zakhi.helpers.matchEachLineOf


fun main() {
    val instructions = matchEachLineOf("aoc2017/day23", Regex("""(\w+) (-?\w+) (-?\w+)""")) { (name, x, y) ->
        CodeInstruction(name, x, y)
    }

    val processor = CoProcessor(instructions)
    processor.run()

    println("The number of mul invocations is ${processor.mulInvocations}")

    val programResult = (106500..123500 step 17).count { !it.isPrime() }
    println("The program result is $programResult")
}


private class CoProcessor(private val instructions: List<CodeInstruction>) {
    private val registers = mutableMapOf<String, Long>().withDefault { 0 }
    private var position = 0

    var mulInvocations = 0
        private set

    fun run() {
        while (position in instructions.indices) {
            val (name, x, y) = instructions[position]
            var jump = 1

            when (name) {
                "set" -> registers[x] = valueOf(y)
                "sub" -> registers[x] = valueOf(x) - valueOf(y)
                "mul" -> registers[x] = valueOf(x) * valueOf(y)
                "jnz" -> if (valueOf(x) != 0L) jump = valueOf(y).toInt()
            }

            if (name == "mul") mulInvocations += 1
            position += jump
        }
    }

    private fun valueOf(value: String): Long = value.toLongOrNull() ?: registers.getValue(value)
}

private data class CodeInstruction(
    val name: String,
    val x: String,
    val y: String
)

private fun Int.isPrime(): Boolean = divisors().count() == 2
