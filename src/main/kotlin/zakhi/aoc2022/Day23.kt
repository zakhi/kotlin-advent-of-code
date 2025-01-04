package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val elves = linesOf("aoc2022/day23").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') x to y else null }
    }.toSet()

    val elfState = ElfState(elves)
    repeat(10) { elfState.move() }
    println("After 10 rounds, there are ${elfState.emptyTiles} empty tiles")

    val longerElfState = ElfState(elves)
    val noElfMoveRound = wholeNumbers().first { !longerElfState.move() }
    println("The elves stop moving after $noElfMoveRound rounds")
}


private class ElfState(initialPositions: Set<Point>) {
    var positions = initialPositions
        private set

    private var direction = Direction.NORTH

    val emptyTiles: Int get() {
        val xRange = positions.maxOf { it.x } - positions.minOf { it.x } + 1
        val yRange = positions.maxOf { it.y } - positions.minOf { it.y } + 1
        return xRange * yRange - positions.size
    }

    fun move(): Boolean {
        val moveProposals = positions.associateWith { position ->
            if (position.allNeighbors.none { it in positions }) return@associateWith position

            val targetDirection = direction.allDirectionsFromThis().find { currentDirection -> currentDirection.targetsFrom(position).none { it in positions } }
            position + (targetDirection?.offset ?: (0 to 0))
        }

        val repeatingTargets = moveProposals.values.groupBy { it }.filterValues { it.size > 1 }.keys

        val newPositions = positions.map { position ->
            val proposedPosition = moveProposals.getValue(position)
            if (proposedPosition in repeatingTargets) position else proposedPosition
        }.toSet()

        if (newPositions == positions) return false

        positions = newPositions
        direction = direction.next()
        return true
    }
}


private enum class Direction(
    val offset: Point,
    val corners: List<Point>
) {
    NORTH(0 to -1, listOf(-1 to -1, 1 to -1)),
    SOUTH(0 to 1, listOf(1 to 1, -1 to 1)),
    WEST(-1 to 0, listOf(-1 to 1, -1 to -1)),
    EAST(1 to 0, listOf(1 to -1, 1 to 1));

    fun targetsFrom(position: Point) = corners.map { position + it } + (position + offset)

    fun next() = entries.cyclicNextFrom(ordinal)

    fun allDirectionsFromThis() = listOf(this, next(), next().next(), next().next().next())
}
