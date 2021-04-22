package zakhi.aoc2015

import kotlin.math.absoluteValue


typealias Point = Pair<Int, Int>

val Point.x get() = first
val Point.y get() = second

fun List<Int>.toPoint(): Point {
    check(size == 2)
    return get(0) to get(1)
}

operator fun Point.plus(offset: Point) = this.x + offset.x to this.y + offset.y

val Point.gridDistance get() = x.absoluteValue + y.absoluteValue

fun grid(xs: Iterable<Int>, ys: Iterable<Int> = xs): Sequence<Point> = xs.asSequence().flatMap { x ->
    ys.asSequence().map { y -> x to y }
}
