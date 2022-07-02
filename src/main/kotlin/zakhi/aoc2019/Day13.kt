package zakhi.aoc2019

import zakhi.helpers.*
import kotlin.math.sign


fun main() {
    val input = readProgramFrom(entireTextOf("aoc2019/day13"))

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
    private val computer = IntCodeComputer(program, ::provideInput, ::handleOutput)
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
