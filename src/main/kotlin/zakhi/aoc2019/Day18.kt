package zakhi.aoc2019

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2019/day18").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') null else (x to y) to char }
    }.toMap()

    val initialPosition = input.firstKeyByValue('@')
    val initialMap = input.mapValues { areaTypeOf(it.value) }
    val steps = locateShortestRoute(TritonMap(initialMap, setOf(initialPosition)))

    println("The shortest path to all keys takes $steps steps")

    val splitInitialPositions = initialPosition.allNeighbors.toSet() - initialPosition.adjacentNeighbors.toSet()
    val splitMap = initialMap.filterKeys { it != initialPosition && it !in initialPosition.adjacentNeighbors }
    val splitSteps = locateShortestRoute(TritonMap(splitMap, splitInitialPositions))

    println("The shortest path to all keys when split to four takes $splitSteps steps")
}


private fun locateShortestRoute(map: TritonMap): Int = with(mutableMapOf<TritonMap, Int>()) {
    fun locate(map: TritonMap): Int = getOrPut(map) {
        map.nextStepMaps.minOfOrNull { (nextMap, distance) -> locate(nextMap) + distance } ?: 0
    }

    return locate(map)
}

private data class TritonMap(
    val map: Map<Point, AreaType>,
    val positions: Set<Point>,
    val keysFound: Set<Key> = emptySet()
) {
    val keysLeft = map.values.filterIsInstance<Key>() - keysFound

    val nextStepMaps: Sequence<Pair<TritonMap, Int>> get() =
        positions.asSequence().map { it to findKeys(it) }.flatMap { (currentPosition, nextFoundKeys) ->
            nextFoundKeys.map { (key, distance) ->
                val newPosition = map.firstKeyByValue(key)
                copy(positions = positions - currentPosition + newPosition, keysFound = keysFound + key) to distance
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TritonMap) return false
        if (positions != other.positions) return false
        if (keysFound != other.keysFound) return false

        return true
    }

    override fun hashCode(): Int = 31 * positions.hashCode() + keysFound.hashCode()

    private fun findKeys(position: Point) = sequence {
        val queue = mutableListOf(position to 0)
        val remainingKeys = keysLeft.toMutableSet()
        val visited = mutableSetOf<Point>()

        while (remainingKeys.isNotEmpty() && queue.isNotEmpty()) {
            val (currentPosition, distance) = queue.removeFirst()
            visited.add(currentPosition)

            currentPosition.adjacentNeighbors.filter { it in map && it !in visited }.forEach { newPosition ->
                val target = map.getValue(newPosition)

                if (target is Key && target !in keysFound) {
                    remainingKeys.remove(target)
                    yield(target to distance + 1)
                } else if (target !is Door || target.matchingKey in keysFound) {
                    queue.add(newPosition to distance + 1)
                }
            }
        }
    }
}

private sealed interface AreaType
private object Ground : AreaType
private data class Key(val letter: Char) : AreaType

private data class Door(val letter: Char) : AreaType {
    val matchingKey get() = Key(letter.lowercaseChar())
}

private fun areaTypeOf(char: Char) = when (char) {
    '.', '@' -> Ground
    in 'A'..'Z' -> Door(char)
    in 'a'..'z' -> Key(char)
    else -> throw Exception("Invalid character in map $char")
}
