package zakhi.aoc2020

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2020/day11").flatMapIndexed { y, line ->
        line.charList().mapIndexed { x, char -> (x to y) to char }
    }.toMap()

    val finalLayout = transform(input)
    println("The number of occupied seats is ${finalLayout.totalOccupiedSeats}")

    val finalLayoutWithSeatVisibility = transform(input, visibleSeatsOnly = true)
    println("The number of actual occupied seats is ${finalLayoutWithSeatVisibility.totalOccupiedSeats}")
}


private typealias SeatLayout = Map<Point, Char>

private fun transform(start: SeatLayout, visibleSeatsOnly: Boolean = false): SeatLayout {
    val occupiedSeatsForClear = if (visibleSeatsOnly) 5 else 4
    var layout = start

    while (true) {
        val newLayout = layout.mapValues { (point, char) ->
            val occupiedCount = if (visibleSeatsOnly) layout.visibleOccupiedSeatsFrom(point) else layout.adjacentOccupiedSeatsOf(point)
            when (char) {
                'L' -> if (occupiedCount == 0) '#' else 'L'
                '#' -> if (occupiedCount >= occupiedSeatsForClear) 'L' else '#'
                else -> char
            }
        }

        if (newLayout == layout) return newLayout
        layout = newLayout
    }
}

private fun SeatLayout.adjacentOccupiedSeatsOf(point: Point): Int {
    return point.allNeighbors.count { get(it) == '#' }
}

private fun SeatLayout.visibleOccupiedSeatsFrom(point: Point): Int {
    return (0 to 0).allNeighbors.count { offset ->
        val firstSeat = wholeNumbers().map { point + offset * it }.takeWhile { it in keys }
            .map { getValue(it) }.firstOrNull { it != '.' }

        firstSeat == '#'
    }
}

private val SeatLayout.totalOccupiedSeats: Int get() = count { it.value == '#' }
