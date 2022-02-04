package zakhi.aoc2019

import zakhi.aoc2019.AmplifierState.*
import zakhi.helpers.digits
import zakhi.helpers.entireTextOf
import zakhi.helpers.permutations


fun main() {
    val program = entireTextOf("aoc2019/day7").trim().split(",").map { it.toInt() }

    val highestSignal = (0..4).toList().permutations().maxOf { phases ->
        findSignalToThrusters(program, phases)
    }

    println("The highest signal is $highestSignal")

    val highestSignalWithFeedbackLoop = (5..9).toList().permutations().maxOf { phases ->
        findSignalToThrusters(program, phases, feedbackLoop = true)
    }

    println("The highest signal with a feedback loop is $highestSignalWithFeedbackLoop")
}


private fun findSignalToThrusters(
    program: List<Int>,
    phases: List<Int>,
    feedbackLoop: Boolean = false
): Int {
    val amplifiers = phases.map { phase -> Amplifier(program, phase) }
    amplifiers.zipWithNext().forEach { (source, target) -> source.sendOutputTo(target) }

    if (feedbackLoop) {
        amplifiers.last().sendOutputTo(amplifiers.first())
    }

    amplifiers.first().receive(0)

    while (amplifiers.any { it.canRun }) {
        amplifiers.first { it.canRun }.run()
    }

    return amplifiers.last().lastOutput
}

private class Amplifier(
    program: List<Int>,
    phase: Int
) {
    private val memory = program.toMutableList()
    private val input = mutableListOf(phase)
    private var output = mutableListOf<Int>()
    private var state = Waiting
    private var position = 0

    fun run() {
        if (state == Terminated) return
        state = Running

        while (state == Running) {
            val currentPosition = position

            when (currentCommand) {
                1 -> set(3, value = arg(1) + arg(2))
                2 -> set(3, value = arg(1) * arg(2))
                3 -> if (input.isEmpty()) waitForInput() else set(1, input.removeFirst())
                4 -> output.add(arg(1))
                5 -> if (arg(1) != 0) position = arg(2)
                6 -> if (arg(1) == 0) position = arg(2)
                7 -> set(3, value = if (arg(1) < arg(2)) 1 else 0)
                8 -> set(3, value = if (arg(1) == arg(2)) 1 else 0)
                99 -> state = Terminated
            }

            if (position == currentPosition && state == Running) {
                position += currentCommandSize
            }
        }
    }

    fun receive(signal: Int) {
        input.add(signal)
    }

    fun sendOutputTo(target: Amplifier) {
        output = target.input
    }

    val canRun: Boolean get() = state == Waiting && input.isNotEmpty()
    val lastOutput: Int get() = output.last()

    private val currentCommand get() = memory[position] % 100

    private val currentCommandSize get() = when (currentCommand) {
        in 3..4 -> 2
        in 5..6 -> 3
        else -> 4
    }

    private fun waitForInput() {
        state = Waiting
    }

    private fun arg(index: Int): Int {
        val argument = memory[position + index]
        val immediateValue = memory[position].digits.reversed().drop(2).getOrNull(index - 1) == 1

        return if (immediateValue) argument else memory[argument]
    }

    private fun set(index: Int, value: Int) {
        memory[memory[position + index]] = value
    }
}

private enum class AmplifierState {
    Running, Waiting, Terminated
}
