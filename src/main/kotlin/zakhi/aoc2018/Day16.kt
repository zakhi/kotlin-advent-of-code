package zakhi.aoc2018

import zakhi.helpers.entireTextOf
import zakhi.helpers.mapDestructured


fun main() {
    val input = entireTextOf("aoc2018/day16")
    val samples = Regex("""Before: \[([\d, ]+)]\n([\d ]+)\nAfter:\s+\[([\d, ]+)]""").findAll(input).mapDestructured {
         (before, instruction, after) -> Sample(before, instruction, after)
    }

    val program = input.lines().drop(samples.size * 4).filterNot { it.isBlank() }.map { it.toInts() }

    val sampleMatches = samples.associateWith { sample -> operations.filter { sample.matches(it) } }
    val matchingSamples = sampleMatches.count { it.value.size >= 3 }
    println("The number of matching samples is $matchingSamples")

    val runner = ProgramRunner(operationMapFrom(sampleMatches))
    runner.run(program)

    println("The value is register 0 after running the program is ${runner.registers[0]}")
}


private typealias Registers = List<Int>
private typealias Instruction = List<Int>

private fun operationMapFrom(sampleMatches: Map<Sample, List<Operation>>): Map<Int, Operation> {
    val matches = sampleMatches.keys.groupBy { it.instruction.first() }.mapValues { (_, samples) ->
        samples.map { sampleMatches.getValue(it).toSet() }.reduce(Set<Operation>::intersect).toMutableSet()
    }

    while (matches.values.any { it.size > 1 }) {
        matches.filter { it.value.size == 1 }.forEach { (code, singleOperation) ->
            matches.filter { it.key != code }.forEach { (_, operations) ->
                operations.remove(singleOperation.first())
            }
        }
    }

    return matches.mapValues { it.value.first() }
}

private class Sample(
    before: String,
    instruction: String,
    after: String
) {
    private val before = before.toInts(separator = ", ")
    private val after = after.toInts(separator = ", ")
    val instruction = instruction.toInts()

    fun matches(operation: Operation): Boolean = operation(before, instruction) == after
}

private class ProgramRunner(private val operationMap: Map<Int, Operation>) {
    var registers = (1..4).map { 0 }
        private set

    fun run(program: List<Instruction>) {
        program.forEach { instruction ->
            val operation = operationMap.getValue(instruction.first())
            registers = operation(registers, instruction)
        }
    }
}

private class OperationContext(
    registers: Registers,
    private val instruction: Instruction
) {
    private val registers = registers.toMutableList()

    val registerValues get() = registers.toList()

    fun register(instructionIndex: Int): Int = registers[at(instructionIndex)]
    fun value(instructionIndex: Int): Int = at(instructionIndex)

    fun setRegister(instructionIndex: Int, value: Int) {
        registers[at(instructionIndex)] = value
    }

    private fun at(index: Int): Int = instruction[index]
}

private class Operation(private val action: OperationContext.() -> Unit) {
    operator fun invoke(registers: Registers, instruction: Instruction): Registers {
        val context = OperationContext(registers, instruction).apply(action)
        return context.registerValues.toList()
    }
}

private val operations = listOf(
    Operation { setRegister(3, register(1) + register(2)) },
    Operation { setRegister(3, register(1) + value(2)) },
    Operation { setRegister(3, register(1) * register(2)) },
    Operation { setRegister(3, register(1) * value(2)) },
    Operation { setRegister(3, register(1).and(register(2))) },
    Operation { setRegister(3, register(1).and(value(2))) },
    Operation { setRegister(3, register(1).or(register(2))) },
    Operation { setRegister(3, register(1).or(value(2))) },
    Operation { setRegister(3, register(1)) },
    Operation { setRegister(3, value(1)) },
    Operation { setRegister(3, if (value(1) > register(2)) 1 else 0) },
    Operation { setRegister(3, if (register(1) > value(2)) 1 else 0) },
    Operation { setRegister(3, if (register(1) > register(2)) 1 else 0) },
    Operation { setRegister(3, if (value(1) == register(2)) 1 else 0) },
    Operation { setRegister(3, if (register(1) == value(2)) 1 else 0) },
    Operation { setRegister(3, if (register(1) == register(2)) 1 else 0) }
)

private fun String.toInts(separator: String = " ") = split(separator).map { it.toInt() }