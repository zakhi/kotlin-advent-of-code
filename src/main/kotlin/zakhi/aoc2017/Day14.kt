package zakhi.aoc2017

import zakhi.helpers.Point
import zakhi.helpers.adjacentNeighbors
import zakhi.helpers.entireTextOf
import zakhi.helpers.join


fun main() {
    val input = entireTextOf("aoc2017/day14").trim()
    val rows = (0..127).map { knotHash("$input-$it").toList() }

    val usedSquared = rows.flatten().sumOf { it.countOneBits() }
    println("The number of used squares is $usedSquared")

    val markedPoints = rows.flatMapIndexed { row, squares ->
        squares.join { it.toString(2).padStart(8, '0') }.mapIndexedNotNull { column, c ->
            if (c == '1') column to row else null
        }
    }

    val numberOfRegions = countRegions(markedPoints)
    println("The number of regions is $numberOfRegions")
}


private fun countRegions(points: List<Point>): Int {
    val pointsLeftToSort = LinkedHashSet(points)
    var numberOfRegions = 0

    while (pointsLeftToSort.isNotEmpty()) {
        var nextPoints = listOf(pointsLeftToSort.removeFirst())
        val region = nextPoints.toMutableSet()
        numberOfRegions += 1

        while (nextPoints.isNotEmpty()) {
            nextPoints = nextPoints.flatMap { point -> point.adjacentNeighbors }.filter { it in pointsLeftToSort }
            pointsLeftToSort.removeAll(nextPoints)
            region.addAll(nextPoints)
        }
    }

    return numberOfRegions
}

private fun <E> LinkedHashSet<E>.removeFirst(): E = first().also { remove(it) }
