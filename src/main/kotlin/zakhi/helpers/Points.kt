package zakhi.helpers

import kotlin.math.absoluteValue


typealias Point = Pair<Int, Int>


val Point.x get() = first
val Point.y get() = second

val Point.column get() = x
val Point.row get() = y

fun List<Int>.toPoint(): Point {
    check(size == 2)
    return get(0) to get(1)
}

operator fun Point.plus(offset: Point) = this.x + offset.x to this.y + offset.y

val Point.gridDistance get() = x.absoluteValue + y.absoluteValue

fun grid(xs: Iterable<Int>, ys: Iterable<Int> = xs): Sequence<Point> = xs.asSequence().flatMap { x ->
    ys.asSequence().map { y -> x to y }
}

val Point.adjacentNeighbors: List<Point>
    get() = listOf(1 to 0, 0 to 1, -1 to 0, 0 to -1).map { this + it }

val Point.allNeighbors: List<Point>
    get() = adjacentNeighbors + listOf(1 to 1, -1 to 1, -1 to -1, 1 to -1).map { this + it }
