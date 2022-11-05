package zakhi.aoc2020

import zakhi.helpers.linesOf
import zakhi.helpers.pairs


fun main() {
    val input = linesOf("aoc2020/day9").map { it.toLong() }.toList()

    val firstInvalidIndex = input.indices.drop(25).first { i ->
        input.slice(i - 25 until i).pairs().none { (a, b) -> a + b == input[i] }
    }

    val invalidNumber = input[firstInvalidIndex]
    println("The first invalid number is $invalidNumber")

    var (start, end) = 0 to 0
    var sum = input.first()

    while (sum != invalidNumber) {
        if (sum < invalidNumber) {
            end += 1
            sum += input[end]
        } else {
            sum -= input[start]
            start += 1
        }
    }

    val range = input.slice(start..end)
    println("The encryption weakness is ${range.min() + range.max()}")
}
