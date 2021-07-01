package zakhi.helpers


fun <T: Any> minimalDistance(
    start: T,
    isTarget: (T) -> Boolean,
    neighbors: (T) -> List<Pair<T, Int>>
): Int {
    val visited = mutableSetOf<T>()
    val heap = minHeapOf(start to 0)

    while (true) {
        val (current, distance) = heap.remove()
        if (isTarget(current)) return distance

        visited.add(current)

        neighbors(current).filterNot { (neighbor, _) -> neighbor in visited }.forEach { (neighbor, neighborDistance) ->
            heap[neighbor] = minOf(heap[neighbor], distance + neighborDistance)
        }
    }
}

fun <T : Any> List<T>.withDefaultDistance(): List<Pair<T, Int>> = map { it to 1 }
