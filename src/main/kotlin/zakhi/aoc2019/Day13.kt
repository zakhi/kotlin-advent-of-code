package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.math.sign


fun main() {
    val input = readProgramFrom(entireTextOf("aoc2019/day13"))

    val game = ArcadeGame(input)
    game.play()

    println("The amount of block tiles is ${game.blockTiles}")

    val workingGame = ArcadeGame(listOf(2L) + input.drop(1))
    workingGame.play()

    println("The game score is ${workingGame.score}")
}


private class ArcadeGame(
    program: List<Long>
) {
    private val computer = IntcodeComputer(program, ::provideInput, ::handleOutput)
    private val tiles = mutableMapOf<Point, Int>().withDefault { 0 }
    private val tileStack = mutableListOf<Int>()

    val blockTiles get() = tiles.count { it.value == 2 }
    val score get() = tiles.getValue(-1 to 0)

    fun play() {
        computer.start()
    }

    fun provideInput(): Long {
        val paddle = tiles.entries.first { it.value == 3 }.key.x
        val ball = tiles.entries.first { it.value == 4 }.key.x

        return (ball - paddle).sign.toLong()
    }

    fun handleOutput(value: Long) {
        if (tileStack.size < 2) {
            tileStack.add(value.toInt())
        } else {
            val (x, y) = tileStack
            tiles[x to y] = value.toInt()
            tileStack.clear()
        }
    }
}
