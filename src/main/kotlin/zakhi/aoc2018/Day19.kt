package zakhi.aoc2018

import zakhi.helpers.divisors
import zakhi.helpers.linesOf


fun main() {
    val input = linesOf("aoc2018/day19").toList()
    val ipRegister = Regex("""#ip (\d)""").matchEntire(input.first())?.groupValues?.get(1)?.toInt() ?: throw Exception("Cannot determine IP register")

    val program = input.drop(1).map { line ->
        val groups = Regex("""(\w+) (\d+) (\d+) (\d+)""").matchEntire(line)?.groupValues ?: throw Exception("Error parsing: $line")
        ProgramInstruction(groups[1], groups.drop(2).map { it.toInt() })
    }

    val device = Device(ipRegister)
    device.run(program)
    println("The value in register 0 is ${device[0]}")

    val result = 10551348.divisors().sum()
    println("The background process ends with register 0 is $result")
}


private class Device(
    private val ipRegister: Int
) {
    private val registers = (0..5).associateWith { 0 }.toMutableMap()
    private var ip = 0

    operator fun get(register: Int) = registers[register]

    operator fun set(register: Int, value: Int) {
        registers[register] = value
    }

    fun run(program: List<ProgramInstruction>) {
        while (ip in program.indices) {
            val instruction = program[ip]
            registers[ipRegister] = ip

            with(instruction) {
                registers[value(3)] = when (operation) {
                    "addr" -> register(1) + register(2)
                    "addi" -> register(1) + value(2)
                    "mulr" -> register(1) * register(2)
                    "muli" -> register(1) * value(2)
                    "banr" -> register(1) and register(2)
                    "bani" -> register(1) and value(2)
                    "borr" -> register(1) or register(2)
                    "bori" -> register(1) or value(2)
                    "setr" -> register(1)
                    "seti" -> value(1)
                    "gtir" -> if (value(1) > register(2)) 1 else 0
                    "gtri" -> if (register(1) > value(2)) 1 else 0
                    "gtrr" -> if (register(1) > register(2)) 1 else 0
                    "eqir" -> if (value(1) == register(2)) 1 else 0
                    "eqri" -> if (register(1) == value(2)) 1 else 0
                    "eqrr" -> if (register(1) == register(2)) 1 else 0
                    else -> throw Exception("Unknown operation $operation")
                }
            }

            ip = registers.getValue(ipRegister) + 1
        }
    }

    private fun ProgramInstruction.value(position: Int) = parameters[position - 1]
    private fun ProgramInstruction.register(position: Int) = registers.getValue(value(position))
}

private data class ProgramInstruction(
    val operation: String,
    val parameters: List<Int>
)
