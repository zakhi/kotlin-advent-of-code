package zakhi.aoc2017

import zakhi.aoc2017.Direction.Down
import zakhi.helpers.*


fun main() {
    val path = linesOf("aoc2017/day19").flatMapIndexed { row, line ->
        line.mapIndexedNotNull { col, char -> if (char != ' ') (col to row) to char else null }
    }.toMap()

    var packetLocation = PathVector(position = path.keys.first { it.y == 0 }, direction = Down)
    var steps = 0
    val letters = mutableListOf<Char>()

    while (packetLocation.position in path) {
        packetLocation = path.step(from = packetLocation)
        steps += 1
        path[packetLocation.position]?.takeIf { it in 'A'..'Z' }?.let { letters.add(it) }
    }

    println("The letters along the path are ${letters.join()}")
    println("The number of steps taken is $steps")
}


private fun Map<Point, Char>.step(from: PathVector): PathVector = when (getValue(from.position)) {
    '+' -> {
        val nextDirection = from.direction.perpendicularDirections.first { from.position + it.offset in keys }
        val nextPosition = from.position + nextDirection.offset
        PathVector(nextPosition, nextDirection)
    }
    else -> from.copy(position = from.position + from.direction.offset)
}

private data class PathVector(
    val position: Point,
    val direction: Direction
)

private enum class Direction(val offset: Point) {
    Up(0 to -1),
    Right(1 to 0),
    Down(0 to 1),
    Left(-1 to 0);

    val perpendicularDirections: List<Direction> get() = when (this) {
        Up, Down -> listOf(Left, Right)
        else -> listOf(Up, Down)
    }
}
