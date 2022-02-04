package zakhi.aoc2019

import zakhi.helpers.*


private const val width = 25
private const val height = 6
private const val layerSize = width * height

fun main() {
    val input = entireTextOf("aoc2019/day8").trim().map { it.digitToInt() }
    val layers = (0 until input.size / layerSize).map { i -> input.layer(i) }

    val checkedLayer = layers.minBy { it.count(0) }
    val checksum = checkedLayer.count(1) * checkedLayer.count(2)

    println("The checksum is $checksum")
    println("The message is")

    repeat(height) { y ->
        repeat(width) { x ->
            val value = layers.map { it[x to y] }.first { it != 2 }
            print(if (value == 0) " " else "█")
        }

        println()
    }
}


private fun List<Int>.layer(index: Int) = Layer(slice(index * layerSize until index * layerSize + layerSize))

private class Layer(private val digits: List<Int>) {
    operator fun get(position: Point) = digits[position.y * width + position.x]

    fun count(digit: Int) = digits.count { it == digit }
}
