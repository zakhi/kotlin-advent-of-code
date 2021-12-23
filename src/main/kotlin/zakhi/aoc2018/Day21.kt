package zakhi.aoc2018

import zakhi.helpers.linesOf


fun main() {
    val input = linesOf("aoc2018/day21").toList()
    val (ipRegister) = Regex("""#ip (\d)""").matchEntire(input.first())?.destructured ?: throw Exception("Cannot determine IP register")

    val program = input.drop(1).map { line ->
        val (name, a, b, c) = Regex("""(\w+) (\d+) (\d+) (\d+)""").matchEntire(line)?.destructured ?: throw Exception("Error parsing: $line")
        DeviceInstruction(name, a.toInt(), b.toInt(), c.toInt())
    }

    val lowestHaltValue = detectLowestHaltValue(ipRegister.toInt(), program)
    println("The lowest halt value is $lowestHaltValue")

    val highestHaltValue = detectHighestHaltValue()
    println("The highest halt value is $highestHaltValue")
}


private fun detectLowestHaltValue(ipRegister: Int, program: List<DeviceInstruction>): Long {
    val registers = (0..5).map { 0L }.toMutableList()
    var ip = 0

    while (ip != 28 && ip in program.indices) {
        registers[ipRegister] = ip.toLong()
        program[ip].run(registers)

        ip = registers[ipRegister].toInt() + 1
    }

    return registers[1]
}

fun detectHighestHaltValue(): Long {
    val registers = (0..5).map { 0L }.toMutableList()
    val ones = mutableSetOf<Long>()
    var result = 0L

    while (true) {
        registers[4] = registers[1] or 65536
        registers[1] = 12772194

        while (true) {
            registers[1] += registers[4] and 255
            registers[1] = ((registers[1] and 16777215) * 65899) and 16777215

            if (registers[4] < 256) break
            registers[4] =  registers[4] / 256
        }

        if (registers[1] in ones) break
        result = registers[1]
        ones.add(registers[1])
    }

    return result
}

private typealias DeviceRegisters = MutableList<Long>

private data class DeviceInstruction(
    private val name: String,
    private val a: Int,
    private val b: Int,
    private val c: Int
) {

    fun run(registers: DeviceRegisters) {
        operations.getValue(name)(registers, a, b, c)
    }
}

private val operations: Map<String, (DeviceRegisters, Int, Int, Int) -> Unit> = mapOf(
    "addr" to { regs, a, b, c -> regs[c] = regs[a] + regs[b] },
    "addi" to { regs, a, b, c -> regs[c] = regs[a] + b },
    "mulr" to { regs, a, b, c -> regs[c] = regs[a] * regs[b] },
    "muli" to { regs, a, b, c -> regs[c] = regs[a] * b },
    "banr" to { regs, a, b, c -> regs[c] = regs[a] and regs[b] },
    "bani" to { regs, a, b, c -> regs[c] = regs[a] and b.toLong() },
    "borr" to { regs, a, b, c -> regs[c] = regs[a] or regs[b] },
    "bori" to { regs, a, b, c -> regs[c] = regs[a] or b.toLong() },
    "setr" to { regs, a, _, c -> regs[c] = regs[a] },
    "seti" to { regs, a, _, c -> regs[c] = a.toLong() },
    "gtir" to { regs, a, b, c -> regs[c] = if (a > regs[b]) 1 else 0 },
    "gtri" to { regs, a, b, c -> regs[c] = if (regs[a] > b) 1 else 0 },
    "gtrr" to { regs, a, b, c -> regs[c] = if (regs[a] > regs[b]) 1 else 0 },
    "eqir" to { regs, a, b, c -> regs[c] = if (a.toLong() == regs[b]) 1 else 0 },
    "eqri" to { regs, a, b, c -> regs[c] = if (regs[a] == b.toLong()) 1 else 0 },
    "eqrr" to { regs, a, b, c -> regs[c] = if (regs[a] == regs[b]) 1 else 0 },
)
