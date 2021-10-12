package zakhi.aoc2018

import zakhi.helpers.*


fun main() {
    val points = matchEachLineOf("aoc2018/day10", Regex("""position=< *(-?\d+), +(-?\d+)> velocity=< *(-?\d+), +(-?\d+)>""")) {
        (x, y, dx, dy) -> PointPOfLight(x.toInt() to y.toInt(), dx.toInt() to dy.toInt())
    }

    val messageTime = findMinimalDistanceTime(points)

    println("The message shows:")
    displayPoints(points.map { it.positionAt(messageTime) })
    println("The message appeared after $messageTime seconds")
}

private fun findMinimalDistanceTime(points: List<PointPOfLight>): Int =
    wholeNumbers().zipWithNext().first { (t1, t2) -> points.distanceAt(t2) > points.distanceAt(t1) }.first

private fun displayPoints(points: List<Point>) {
    val xs = points.minOf { it.x } .. points.maxOf { it.x }
    val ys = points.minOf { it.y } .. points.maxOf { it.y }

    ys.forEach { y ->
        println(xs.join { x -> if (x to y in points) "##" else "  "  })
    }
}

private fun List<PointPOfLight>.distanceAt(time: Int): Int {
    val positions = map { it.positionAt(time) }
    return positions.maxOf { it.x } - positions.minOf { it.x }
}

private data class PointPOfLight(
    val position: Point,
    val velocity: Point
) {
    fun positionAt(time: Int): Point = position + velocity * time
}
