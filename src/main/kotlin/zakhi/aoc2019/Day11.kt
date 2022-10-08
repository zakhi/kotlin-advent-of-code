package zakhi.aoc2019

import zakhi.aoc2019.Direction.*
import zakhi.aoc2019.RobotState.*
import zakhi.helpers.*
import java.math.BigInteger


fun main() {
    val input = entireTextOf("aoc2019/day11").trim().split(",").map { it.toBigInteger() }

    val robot = PaintingRobot(input)
    robot.startPainting()

    println("The number of painted tiles is ${robot.numberOfPaintedTiles}")

    val secondRobot = PaintingRobot(input, startOnWhiteTile = true)
    secondRobot.startPainting()

    println("The registration identifier is:")
    secondRobot.printIdentifier()
}


private class PaintingRobot(
    input: List<BigInteger>,
    startOnWhiteTile: Boolean = false
) {
    private val computer = IntCodeComputerV4(input, ::provideInput, ::handleOutput)
    private val paintedTiles = mutableMapOf<Point, Int>().withDefault { 0 }
    private var position = 0 to 0
    private var direction = Up
    private var state = Painting

    init {
        if (startOnWhiteTile) paintedTiles[position] = 1
    }

    val numberOfPaintedTiles get() = paintedTiles.size

    fun startPainting() {
        computer.start()
    }

    fun printIdentifier() {
        val whiteTiles = paintedTiles.filterValues { it == 1 }.keys
        val xs = whiteTiles.minOf { it.x }..whiteTiles.maxOf { it.x }
        val ys = whiteTiles.minOf { it.y }..whiteTiles.maxOf { it.y }

        ys.forEach { y ->
            println(xs.join { x -> if (paintedTiles.getValue(x to y) == 1) "██" else "  " })
        }
    }

    private fun provideInput(): Int = paintedTiles.getValue(position)

    private fun handleOutput(value: Int) {
        when (state) {
            Painting -> paintedTiles[position] = value
            Moving -> {
                direction = if (value == 0) direction.turnLeft() else direction.turnRight()
                position += direction.offset
            }
        }

        state = state.next()
    }
}

private class IntCodeComputerV4(
    program: List<BigInteger>,
    private val inputProvider: () -> Int,
    private val outputConsumer: (Int) -> Unit
) {
    private val memory = program.withIndex().associate { (index, value) -> index.toBigInteger() to value }.toMutableMap().withDefault { BigInteger.ZERO }
    private var position = BigInteger.ZERO
    private var relativeBase = BigInteger.ZERO

    fun start() {
        while (true) {
            val currentPosition = position

            when (currentCommand) {
                1 -> set(3, value = arg(1) + arg(2))
                2 -> set(3, value = arg(1) * arg(2))
                3 -> set(1, inputProvider().toBigInteger())
                4 -> outputConsumer(arg(1).toInt())
                5 -> if (arg(1) != BigInteger.ZERO) position = arg(2)
                6 -> if (arg(1) == BigInteger.ZERO) position = arg(2)
                7 -> set(3, value = if (arg(1) < arg(2)) BigInteger.ONE else BigInteger.ZERO)
                8 -> set(3, value = if (arg(1) == arg(2)) BigInteger.ONE else BigInteger.ZERO)
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
        val offset = if (parameterMode(index) == 2) relativeBase else BigInteger.ZERO
        memory[memory.getValue(position + index.toBigInteger()) + offset] = value
    }

    private fun parameterMode(index: Int) = memory.getValue(position).digits.reversed().drop(2).getOrNull(index - 1) ?: 0

    private val BigInteger.digits: List<Int> get() = toString().map { it.digitToInt() }
}


private enum class RobotState {
    Painting, Moving;

    fun next() = values().toList().cyclicNextFrom(ordinal)
}
