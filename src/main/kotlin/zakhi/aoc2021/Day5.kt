package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val lines = matchEachLineOf("aoc2021/day5", Regex("""(\d+),(\d+) -> (\d+),(\d+)""")) { (a, b, c, d) ->
        Line(a.toInt() to b.toInt(), c.toInt() to d.toInt())
    }

    val nonDiagonalLines = lines.filter { it.horizontal || it.vertical }
    val points = mutableMapOf<Point, Int>()

    nonDiagonalLines.forEach { line ->
        line.points.forEach { points.increment(it) }
    }

    val overlapPoints = points.count { it.value > 1 }
    println("The number of overlap points is $overlapPoints")

    lines.filter { it !in nonDiagonalLines }.forEach { line ->
        line.points.forEach { points.increment(it) }
    }

    val totalOverlapPoints = points.count { it.value > 1 }
    println("The number of total overlap points is $totalOverlapPoints")
}


private data class Line(
    val start: Point,
    val end: Point
) {
    val horizontal get() = start.y == end.y
    val vertical get() = start.x == end.x

    val points: List<Point> get() = when {
        horizontal -> xProgression.map { x -> x to start.y }
        vertical -> yProgression.map { y -> start.x to y }
        else -> xProgression.zip(yProgression)
    }

    private val xProgression get() = if (start.x <= end.x) start.x .. end.x else start.x downTo end.x
    private val yProgression get() = if (start.y <= end.y) start.y .. end.y else start.y downTo end.y
}

private fun <K> MutableMap<K, Int>.increment(key: K) {
    set(key, getOrDefault(key, 0) + 1)
}
