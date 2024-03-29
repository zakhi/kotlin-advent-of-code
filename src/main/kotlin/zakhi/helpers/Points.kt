package zakhi.helpers

import kotlin.math.absoluteValue


typealias Point = Pair<Int, Int>

val Point.x get() = first
val Point.y get() = second

fun List<Int>.toPoint(): Point {
    check(size == 2)
    return get(0) to get(1)
}

operator fun Point.plus(offset: Point) = this.x + offset.x to this.y + offset.y
operator fun Point.minus(offset: Point) = this.x - offset.x to this.y - offset.y
operator fun Point.times(scalar: Int): Point = x * scalar to y * scalar
operator fun Point.div(scalar: Int): Point = x / scalar to y / scalar
fun Point.mod(scalar: Int): Point = x.mod(scalar) to y.mod(scalar)

val Point.gridDistance get() = x.absoluteValue + y.absoluteValue

fun grid(xs: Iterable<Int>, ys: Iterable<Int> = xs): Sequence<Point> = ys.asSequence().flatMap { y ->
    xs.asSequence().map { x -> x to y }
}

val Point.adjacentNeighbors: List<Point>
    get() = listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1).map { this + it }

val Point.diagonalNeighbors: List<Point>
    get() = listOf(1 to 1, -1 to 1, -1 to -1, 1 to -1).map { this + it }

val Point.allNeighbors: List<Point>
    get() = adjacentNeighbors + diagonalNeighbors


fun printPoints(points: Iterable<Point>) {
    val xs = points.minOf { it.x } .. points.maxOf { it.x }
    val ys = points.minOf { it.y } .. points.maxOf { it.y }

    ys.forEach { y ->
        println(xs.join { x -> if (x to y in points) "##" else "  "  })
    }
}
