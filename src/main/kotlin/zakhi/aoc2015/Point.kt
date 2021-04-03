package zakhi.aoc2015


typealias Point = Pair<Int, Int>

val Point.x get() = first
val Point.y get() = second

fun List<Int>.toPoint(): Point {
    check(size == 2)
    return get(0) to get(1)
}
