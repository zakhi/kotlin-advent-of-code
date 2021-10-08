package zakhi.aoc2016

import zakhi.helpers.*


fun main() {
    val nodes = linesOf("aoc2016/day22").drop(2).map { line ->
        val match = Regex("""/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T.*""").matchEntire(line) ?: throw Exception("Invalid line $line")
        val (x, y, size, used) = match.destructured
        Node(x.toInt() to y.toInt(), size.toInt(), used.toInt())
    }.toList()

    val validPairs = nodes.orderedPairs().count { (a, b) ->
        a.used > 0 && b.available >= a.used
    }

    println("The number of valid pairs is $validPairs")

    val nodesByPosition = nodes.associateBy { it.position }
    val bucketNode = nodes.maxBy { it.available }
    val distanceToSource = nodes.maxOf { it.position.x }
    val goalNode = nodesByPosition.getValue(distanceToSource to 0)

    val distanceToGoal = minimalDistance(
        start = bucketNode,
        isTarget = { it == goalNode },
        neighbors = { node ->
            node.position.adjacentNeighbors.mapNotNull { nodesByPosition[it] }
                .filter { it.used <= bucketNode.size }.withDefaultDistance()
        }
    )

    val totalSteps = distanceToGoal + (distanceToSource - 1) * 5
    println("The number of steps to move the goal data is $totalSteps")
}


private data class Node(
    val position: Point,
    val size: Int,
    val used: Int
) {
    val available = size - used
}
