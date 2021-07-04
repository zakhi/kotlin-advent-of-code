package zakhi.aoc2016

import zakhi.helpers.second

class Computer {
    val registers = ('a'..'d').associate { it.toString() to 0 }.toMutableMap()

    fun run(program: List<Instruction>) {
        val instructions = program.toMutableList()
        var pointer = 0

        while (pointer in instructions.indices) {
            val (code, arguments) = instructions[pointer]
            pointer += when (code) {
                "cpy" -> copy(arguments.first(), arguments.second())
                "inc" -> update(arguments.first(), 1)
                "dec" -> update(arguments.first(), -1)
                "jnz" -> jump(arguments.first(), arguments.second())
                "tgl" -> toggle(arguments.first(), pointer, instructions)
                else -> throw Exception("Unknown instruction $code")
            }
        }
    }

    private fun copy(source: String, target: String): Int {
        if (target in registers) registers[target] = registerValueOrNumber(source)
        return 1
    }

    private fun update(target: String, amount: Int): Int {
        if (target in registers) registers[target] = registers.getValue(target) + amount
        return 1
    }

    private fun jump(source: String, amount: String): Int {
        val value = registerValueOrNumber(source)
        val jumpDistance = registerValueOrNumber(amount)

        return if (value != 0) jumpDistance else 1
    }

    private fun toggle(source: String, pointer: Int, instructions: MutableList<Instruction>): Int {
        val index = registerValueOrNumber(source) + pointer

        if (index in instructions.indices) {
            val (code, arguments) = instructions[index]

            val newCode = when (arguments.size) {
                1 -> if (code == "inc") "dec" else "inc"
                else -> if (code == "jnz") "cpy" else "jnz"
            }

            instructions[index] = Instruction(newCode, arguments)
        }

        return 1
    }

    private fun registerValueOrNumber(source: String) = source.toIntOrNull() ?: registers.getValue(source)
}

data class Instruction(
    val code: String,
    val arguments: List<String>
)
