package zakhi.aoc2017

import zakhi.helpers.entireTextOf
import zakhi.helpers.join
import zakhi.helpers.second


fun main() {
    val input = entireTextOf("aoc2017/day10").trim()

    val lengths = input.split(",").map { it.toInt() }
    val result = knotTransform(lengths)
    println("The multiplication of the first two numbers is ${result.first() * result.second()}")

    val hash = knotHash(input).join { it.toString(16).padStart(2, '0') }
    println("The hash is $hash")
}
