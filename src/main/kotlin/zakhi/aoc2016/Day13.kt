package zakhi.aoc2016

import zakhi.Point
import zakhi.input
import zakhi.minHeapOf
import zakhi.numbers.isEven
import zakhi.points.plus
import zakhi.points.x
import zakhi.points.y


fun main() {
    val officeDesignerNumber = input.entireTextOf("aoc2016/day13").trim().toInt()
    val maze = Maze(officeDesignerNumber)

    println("The fewest number of steps is ${maze.shortestRouteTo(31 to 39)}")
    println("The number of locations within 50 steps is ${maze.locationsReachableIn(50)}")
}


private class Maze(private val officeDesignerNumber: Int) {
    private val visited = mutableMapOf<Point, Int>()

    fun shortestRouteTo(target: Point): Int {
        val startPosition = 1 to 1
        val heap = minHeapOf(startPosition to 0)

        while (true) {
            val (position, distance) = heap.remove()
            if (position == target) return distance

            visited[position] = distance

            openSpacesNextTo(position).filterNot { it in visited }.forEach {
                heap[it] = minOf(heap[it], distance + 1)
            }
        }
    }

    fun locationsReachableIn(steps: Int): Int = visited.count { it.value <= steps }

    private fun openSpacesNextTo(position: Point): Sequence<Point> =
        sequenceOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
            .map { (position + it) }
            .filter { it.x >= 0 && it.y >= 0 }
            .filter { it.isOpenSpace }

    private val Point.isOpenSpace: Boolean get() =
        (x * x + 3 * x + 2 * x * y + y + y * y + officeDesignerNumber).toString(2).count { it == '1' }.isEven
}
