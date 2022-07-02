package zakhi.aoc2019

import zakhi.helpers.digits
import zakhi.helpers.join


fun readProgramFrom(text: String): List<Int> = text.trim().split(",").map { it.toInt() }

class IntCodeComputer(
    program: List<Int>,
    private val inputProvider: () -> Int,
    private val outputConsumer: (Int) -> Unit
) {
    private val memory = program.withIndex().associate { (index, value) -> index to value }.toMutableMap().withDefault { 0 }
    private var position = 0
    private var relativeBase = 0
    private var stopped: Boolean = false

    fun start() {
        stopped = false

        while (!stopped) {
            val currentPosition = position

            when (currentCommand) {
                1 -> set(3, value = arg(1) + arg(2))
                2 -> set(3, value = arg(1) * arg(2))
                3 -> set(1, inputProvider())
                4 -> outputConsumer(arg(1))
                5 -> if (arg(1) != 0) position = arg(2)
                6 -> if (arg(1) == 0) position = arg(2)
                7 -> set(3, value = if (arg(1) < arg(2)) 1 else 0)
                8 -> set(3, value = if (arg(1) == arg(2)) 1 else 0)
                9 -> relativeBase += arg(1)
                99 -> return
            }

            if (position == currentPosition) {
                position += currentCommandSize
            }
        }
    }

    fun stop() {
        stopped = true
    }

    private val currentCommand get() = memory.getValue(position).digits.takeLast(2).join().toInt()

    private val currentCommandSize get() = when (currentCommand) {
        in 3..4 -> 2
        in 5..6 -> 3
        9 -> 2
        else -> 4
    }

    private fun arg(index: Int): Int {
        val argument = memory.getValue(position + index)

        return when (parameterMode(index)) {
            1 -> argument
            2 -> memory.getValue(relativeBase + argument)
            else -> memory.getValue(argument)
        }
    }

    private fun set(index: Int, value: Int) {
        val offset = if (parameterMode(index) == 2) relativeBase else 0
        memory[memory.getValue(position + index) + offset] = value
    }

    private fun parameterMode(index: Int) = memory.getValue(position).digits.reversed().drop(2).getOrNull(index - 1) ?: 0
}
