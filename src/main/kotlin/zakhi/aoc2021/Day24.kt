package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val operations = linesOf("aoc2021/day24").chunked(18).map { lines ->
        val (correction) = Regex("""add x (-?\d+)""").matchEntire(lines[5])?.destructured ?: fail("cannot find correction at $lines")
        val (offset) = Regex("""add y (\d+)""").matchEntire(lines[15])?.destructured ?: fail("cannot find offset at $lines")
        val (divideBy) = Regex("""div z (1|26)""").matchEntire(lines[4])?.destructured ?: fail("cannot find divideBy at $lines")

        when {
            divideBy.toInt() == 1 -> MultiplyOperation(correction.toInt(), offset.toInt())
            else -> DivideOperation(correction.toInt(), offset.toInt())
        }
    }.toList()

    val largest = findBestModelNumber(operations, 9.downTo(1))
    println("the largest model number is ${largest.join()}")

    val smallest = findBestModelNumber(operations, 1..9)
    println("the smallest model number is ${smallest.join()}")
}


private fun findBestModelNumber(operations: List<Operation>, progression: IntProgression): List<Int> {
    val stack = stackOf<MultiplyOperation>()
    val result = mutableListOf<Int>()

    operations.forEach { operation ->
        when (operation) {
            is MultiplyOperation -> stack.add(operation)
            is DivideOperation -> {
                val pairedOperation = stack.pop()
                val difference = operation.differenceFrom(pairedOperation)

                val best = progression.first { it + difference in 1..9 }
                result.add(0, best)
                result.add(best + difference)
            }
        }
    }
    return result
}

private sealed interface Operation

private data class MultiplyOperation(
    val xOffset: Int,
    val yOffset: Int,
) : Operation

private data class DivideOperation(
    val xOffset: Int,
    val yOffset: Int
) : Operation {
    fun differenceFrom(multiplyOperation: MultiplyOperation): Int = multiplyOperation.yOffset + xOffset
}