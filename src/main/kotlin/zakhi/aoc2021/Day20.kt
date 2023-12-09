package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2021/day20").toList()
    val algorithm = input.first()

    val source = input.drop(2).flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') x to y else null }
    }.toSet()

    val twoTimeEnhanced = (1..2).fold(source) { image, time -> enhance(image, algorithm, litBackground = time % 2 == 0) }
    println("The number of pixels that are on after two enhancements is ${twoTimeEnhanced.size}")

    val fiftyTimeEnhanced = (1..50).fold(source) { image, time -> enhance(image, algorithm, litBackground = time % 2 == 0) }
    println("The number of pixels that are on after fifty enhancements is ${fiftyTimeEnhanced.size}")
}


private fun enhance(image: Set<Point>, algorithm: String, litBackground: Boolean = false): Set<Point> {
    val imageDimensions = GridDimensions(image.minOf { it.x }..image.maxOf { it.x }, image.minOf { it.y }..image.maxOf { it.y })
    val newDimensions = imageDimensions.enlargeBy(1)

    return grid(newDimensions.xs, newDimensions.ys).mapNotNull { point ->
        val code = considerationGrid.map { if ((point + it) in image || (point + it !in imageDimensions && litBackground)) "1" else "0" }.join().toInt(2)
        if (algorithm[code] == '#') point else null
    }.toSet()
}

private val considerationGrid = grid(-1..1, -1..1).toList()

private data class GridDimensions(
    val xs: IntRange,
    val ys: IntRange
) {

    operator fun contains(point: Point): Boolean = point.x in xs && point.y in ys

    fun enlargeBy(factor: Int): GridDimensions =
        GridDimensions((xs.first - factor)..(xs.last + factor), (ys.first - factor)..(ys.last + factor))
}
