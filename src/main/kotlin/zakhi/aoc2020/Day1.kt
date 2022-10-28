package zakhi.aoc2020

import zakhi.helpers.combinations
import zakhi.helpers.linesOf
import zakhi.helpers.pairs


fun main() {
    val input = linesOf("aoc2020/day1").map { it.toInt() }.toList()
    val (first, second) = input.pairs().first { (a, b) -> a + b == 2020 }
    println("The product of the 2020 pair is ${first * second}")

    val triplet = input.combinations(3).first { it.sum() == 2020 }
    val tripletProduct = triplet.reduce(Int::times)
    println("The product of the 2020 triplet is $tripletProduct")
}
