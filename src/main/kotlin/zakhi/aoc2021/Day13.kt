package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val (dotsInput, foldsInput) = entireTextOf("aoc2021/day13").trim().split("\n\n")
    val dots = dotsInput.lines().map { line -> line.split(",").map { it.toInt() }.pairs().first() }.toSet()
    val folds = foldsInput.lines().map { parseFold(it) }

    val dotsAfterFirstFold = folds.first().fold(dots)
    println("The number of visible dots after the first folds is ${dotsAfterFirstFold.size}")

    val finalDots = folds.fold(dots) { currentDots, fold -> fold.fold(currentDots) }

    println("The code is")
    printPoints(finalDots)
}


private data class Fold(
    val axis: String,
    val value: Int
) {
    fun fold(dots: Set<Point>): Set<Point> = dots.map { (x, y) ->
        when (axis) {
            "x" -> (if (x < value) x else value * 2 - x) to y
            "y" -> x to (if (y < value) y else value * 2 - y)
            else -> fail("invalid fold axis $axis")
        }
    }.toSet()
}

private fun parseFold(line: String): Fold {
    val (axis, value) = Regex("""fold along ([xy])=(\d+)""").matchEntire(line)?.destructured ?: fail("Invalid fold: $line")
    return Fold(axis, value.toInt())
}
