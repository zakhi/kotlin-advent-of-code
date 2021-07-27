package zakhi.aoc2017

import zakhi.helpers.linesOf
import zakhi.helpers.wholeNumbers


fun main() {
    val input = linesOf("aoc2017/day5").map { it.toInt() }.toList()

    val steps = Jumps(input).run()
    println("The number of steps needed is $steps")

    val stepsWithDecrease = Jumps(input, supportDecrease = true).run()
    println("The number of steps needed is $stepsWithDecrease")
}


private class Jumps(
    original: List<Int>,
    private val supportDecrease: Boolean = false
) {
    private val instructions = original.toMutableList()
    private var pointer = 0

    fun run(): Int = wholeNumbers().onEach { jump() }.first { pointer !in instructions.indices }

    private fun jump() {
        val distance = instructions[pointer]
        instructions[pointer] += if (supportDecrease && instructions[pointer] >= 3) -1 else 1
        pointer += distance
    }
}
