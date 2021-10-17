package zakhi.aoc2018

import zakhi.helpers.Point
import zakhi.helpers.entireTextOf
import zakhi.helpers.grid
import zakhi.helpers.maxBy


fun main() {
    val serial = entireTextOf("aoc2018/day11").trim().toInt()
    val grid = PowerGrid(serial)

    val bestSized3 = grid.highestSquareOfSize(3)
    println("The top-left coordinate of the highest 3-size square is ${bestSized3.topLeft}")

    val bestAnySize = grid.highestSquare()
    println("The top-left coordinate of the highest square of any size is $bestAnySize")
}


private class PowerGrid(serial: Int) {
    private val sums: Map<Point, Int> by lazy {
        val powers = grid(1..300).associateWith { (x, y) ->
            val rackId = x + 10
            val power = ((rackId * y) + serial) * rackId
            power.div(100).mod(10) - 5
        }

        mutableMapOf<Point, Int>().withDefault { 0 }.also { sums ->
            grid(1..300).forEach { (x, y) ->
                sums[x to y] = powers.getValue(x to y) +
                    sums.getValue(x - 1 to y) +
                    sums.getValue(x to y - 1) -
                    sums.getValue(x - 1 to y - 1)
            }
        }
    }

    fun highestSquareOfSize(size: Int): SubSquare = allSubSquares(size).maxBy { it.sum() }

    fun highestSquare(): SubSquare = (1..300).asSequence().flatMap { allSubSquares(it) }.maxBy { it.sum() }

    private fun allSubSquares(size: Int) = grid(1..(300 - size + 1)).map { (x, y) -> SubSquare(x, y, size) }

    private fun SubSquare.sum(): Int = sums.getValue(x + size - 1 to y + size - 1) -
            sums.getValue(x + size - 1 to y - 1) -
            sums.getValue(x - 1 to y + size - 1) +
            sums.getValue(x - 1 to y - 1)
}

private data class SubSquare(
    val x: Int,
    val y: Int,
    val size: Int
) {
    val topLeft = "$x,$y"

    override fun toString(): String = "$x,$y,$size"
}
