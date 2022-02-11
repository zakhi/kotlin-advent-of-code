package zakhi.aoc2019

import zakhi.helpers.entireTextOf
import zakhi.helpers.join
import java.math.BigInteger
import java.math.BigInteger.*


fun main() {
    val program = entireTextOf("aoc2019/day9").trim().split(",").map { it.toBigInteger() }
    val testModeComputer = IntCodeComputerV3(program, input = 1)
    testModeComputer.run()

    println("The test keycode produced is ${testModeComputer.output().last()}")

    val sensorModeComputer = IntCodeComputerV3(program, input = 2)
    sensorModeComputer.run()

    println("The sensor keycode produced is ${sensorModeComputer.output().last()}")
}


private class IntCodeComputerV3(program: List<BigInteger>, input: Int) {
    private val memory = program.withIndex().associate { (index, value) -> index.toBigInteger() to value }.toMutableMap().withDefault { ZERO }
    private val input = mutableListOf(input.toBigInteger())
    private val output = mutableListOf<BigInteger>()
    private var position = ZERO
    private var relativeBase = ZERO

    fun output() = output.toList()

    fun run() {
        while (true) {
            val currentPosition = position

            when (currentCommand) {
                1 -> set(3, value = arg(1) + arg(2))
                2 -> set(3, value = arg(1) * arg(2))
                3 -> set(1, input.removeFirst())
                4 -> output.add(arg(1))
                5 -> if (arg(1) != ZERO) position = arg(2)
                6 -> if (arg(1) == ZERO) position = arg(2)
                7 -> set(3, value = if (arg(1) < arg(2)) ONE else ZERO)
                8 -> set(3, value = if (arg(1) == arg(2)) ONE else ZERO)
                9 -> relativeBase += arg(1)
                99 -> return
            }

            if (position == currentPosition) {
                position += currentCommandSize
            }
        }
    }

    private val currentCommand get() = memory.getValue(position).digits.takeLast(2).join().toInt()

    private val currentCommandSize get() = when (currentCommand) {
        in 3..4 -> 2
        in 5..6 -> 3
        9 -> 2
        else -> 4
    }.toBigInteger()

    private fun arg(index: Int): BigInteger {
        val argument = memory.getValue(position + index.toBigInteger())

        return when (parameterMode(index)) {
            1 -> argument
            2 -> memory.getValue(relativeBase + argument)
            else -> memory.getValue(argument)
        }
    }

    private fun set(index: Int, value: BigInteger) {
        val offset = if (parameterMode(index) == 2) relativeBase else ZERO
        memory[memory.getValue(position + index.toBigInteger()) + offset] = value
    }

    private fun parameterMode(index: Int) = memory.getValue(position).digits.reversed().drop(2).getOrNull(index - 1) ?: 0

    private val BigInteger.digits: List<Int> get() = toString().map { it.digitToInt() }
}
