package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.math.sign


fun main() {
    val input = entireTextOf("aoc2019/day13").trim().split(",").map { it.toInt() }

    val game = ArcadeGame(input)
    game.play()

    println("The amount of block tiles is ${game.blockTiles}")

    val workingGame = ArcadeGame(listOf(2) + input.drop(1))
    workingGame.play()

    println("The game score is ${workingGame.score}")
}


private class ArcadeGame(
    program: List<Int>
) {
    private val computer = IntCodeComputerV5(program, ::provideInput, ::handleOutput)
    private val tiles = mutableMapOf<Point, Int>().withDefault { 0 }
    private val tileStack = mutableListOf<Int>()

    val blockTiles get() = tiles.count { it.value == 2 }
    val score get() = tiles.getValue(-1 to 0)

    fun play() {
        computer.start()
    }

    fun provideInput(): Int {
        val paddle = tiles.entries.first { it.value == 3 }.key.x
        val ball = tiles.entries.first { it.value == 4 }.key.x

        return (ball - paddle).sign
    }

    fun handleOutput(value: Int) {
        if (tileStack.size < 2) {
            tileStack.add(value)
        } else {
            val (x, y) = tileStack
            tiles[x to y] = value
            tileStack.clear()
        }
    }
}

private class IntCodeComputerV5(
    program: List<Int>,
    private val inputProvider: () -> Int,
    private val outputConsumer: (Int) -> Unit
) {
    private val memory = program.withIndex().associate { (index, value) -> index to value }.toMutableMap().withDefault { 0 }
    private var position = 0
    private var relativeBase = 0

    fun start() {
        while (true) {
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
