package zakhi.aoc2016

import zakhi.helpers.isOdd
import zakhi.helpers.join
import zakhi.helpers.linesOf
import zakhi.helpers.tryMatch


fun main() {
    val instructions = linesOf("aoc2016/day21").mapNotNull { create(it) }.toList()
    val scrambler = Scrambler(instructions)

    val scrambledPassword = scrambler.scramble("abcdefgh")
    println("The scrambled password is $scrambledPassword")

    val unscrambledPassword = scrambler.unscramble("fbgdceah")
    println("The unscrambled password is $unscrambledPassword")
}


private fun create(instruction: String): ScramblerInstruction = tryMatch(instruction) {
    Regex("""swap position (\d+) with position (\d+)""") to { (a, b) -> SwapPositions(a.toInt(), b.toInt()) }
    Regex("""swap letter (\w) with letter (\w)""") to { (a, b) -> SwapLetters(a.first(), b.first()) }
    Regex("""rotate (right|left) (\d+) steps?""") to { (direction, steps) -> RotateSteps(steps.toInt(), direction) }
    Regex("""rotate based on position of letter (\w)""") to { (letter) -> RotateBasedOnPosition(letter.first()) }
    Regex("""reverse positions (\d+) through (\d+)""") to { (a, b) -> Reverse(a.toInt(), b.toInt()) }
    Regex("""move position (\d+) to position (\d+)""") to { (a, b) -> MovePosition(a.toInt(), b.toInt()) }
} ?: throw Exception("Invalid instruction: $instruction")

private interface ScramblerInstruction {
    fun scramble(value: String): String
    fun unscramble(value: String): String
}

private class SwapPositions(
    private val first: Int,
    private val second: Int
) : ScramblerInstruction {

    override fun scramble(value: String): String = value.swapIndexes(first, second)

    override fun unscramble(value: String): String = scramble(value)
}

private class SwapLetters(
    private val first: Char,
    private val second: Char
) : ScramblerInstruction {

    override fun scramble(value: String): String = value.swapIndexes(value.indexOf(first), value.indexOf(second))

    override fun unscramble(value: String): String = scramble(value)
}

private class RotateSteps(
    steps: Int,
    direction: String
) : ScramblerInstruction {

    private val actualSteps = steps * if (direction == "right") -1 else 1

    override fun scramble(value: String): String = value.rotate(actualSteps)

    override fun unscramble(value: String): String = value.rotate(actualSteps * -1)
}

private class RotateBasedOnPosition(
    private val letter: Char
) : ScramblerInstruction {

    override fun scramble(value: String): String {
        val steps = value.indexOf(letter).let { it + if (it >= 4) 2 else 1 }
        return value.rotate(-steps)
    }

    override fun unscramble(value: String): String {
        val steps = value.indexOf(letter)

        val reverseSteps = when {
            steps == 0 -> 1
            steps.isOdd -> steps / 2 + 1
            else -> steps / 2 - 3 + value.length
        }

        return value.rotate(reverseSteps)
    }
}

private class Reverse(
    first: Int,
    second: Int
): ScramblerInstruction {

    private val range = first..second

    override fun scramble(value: String): String = value.replaceRange(range, value.slice(range).reversed())

    override fun unscramble(value: String): String = scramble(value)
}

private class MovePosition(
    private val source: Int,
    private val target: Int
) : ScramblerInstruction {

    override fun scramble(value: String): String = value.move(source, target)

    override fun unscramble(value: String): String = value.move(target, source)
}

private class Scrambler(private val instructions: List<ScramblerInstruction>) {

    fun scramble(value: String): String =
        instructions.fold(value) { current, instruction -> instruction.scramble(current) }

    fun unscramble(value: String): String =
        instructions.foldRight(value) { instruction, current -> instruction.unscramble(current) }
}

private fun String.swapIndexes(first: Int, second: Int): String {
    val original = this

    return buildString {
        append(original)
        set(first, original[second])
        set(second, original[first])
    }
}

private fun String.rotate(steps: Int): String {
    if (steps == 0) return this

    val actualSteps = steps.mod(length)
    return "${drop(actualSteps)}${take(actualSteps)}"
}

private fun String.move(from: Int, to: Int): String = toCharArray().toMutableList().apply {
    add(to, removeAt(from))
}.join()
