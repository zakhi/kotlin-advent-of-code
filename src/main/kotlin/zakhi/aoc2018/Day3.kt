package zakhi.aoc2018

import zakhi.helpers.Point
import zakhi.helpers.matchEachLineOf


fun main() {
    val claims = matchEachLineOf("aoc2018/day3",
        Regex("""#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""")) { (id, left, top, width, height) ->
        FabricClaim(id.toInt(), left.toInt(), top.toInt(), width.toInt(), height.toInt())
    }

    val overlaps = claims.flatMap { it.squares }.groupingBy { it }.eachCount().filterValues { it > 1 }
    println("The number of overlapping squares is ${overlaps.size}")

    val nonOverlappingClaim = claims.first { claim -> claim.squares.all { it !in overlaps } }
    println("The non-overlapping claim is ${nonOverlappingClaim.id}")
}


private class FabricClaim(
    val id: Int,
    left: Int,
    top: Int,
    width: Int,
    height: Int
) {
    val squares: Set<Point> = (left until left + width).flatMap { x ->
        (top until top + height).map { y -> x to y }
    }.toSet()
}
