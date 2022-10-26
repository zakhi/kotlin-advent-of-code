package zakhi.aoc2019

import zakhi.helpers.charList
import zakhi.helpers.entireTextOf


fun main() {
    val program = readProgramFrom(entireTextOf("aoc2019/day25"))
    InvestigatingDroid(program).investigate()
}

private class InvestigatingDroid(program: List<Long>) {
    private val computer = IntcodeComputer(program, ::provideInput, ::consumeOutput)
    private val buffer = mutableListOf<Long>()

    fun investigate() {
        while (true) computer.start()
    }

    private fun provideInput(): Long {
        if (buffer.isEmpty()) {
            readLine()?.let { text -> buffer.addAll("$text\n".charList().map { it.code.toLong() }) }
        }

        return buffer.removeFirstOrNull() ?: -1L
    }

    private fun consumeOutput(value: Long) {
        print(value.toInt().toChar())
    }
}

