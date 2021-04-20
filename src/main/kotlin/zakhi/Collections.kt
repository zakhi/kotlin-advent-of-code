package zakhi


fun <E> List<E>.permutations(): List<List<E>> {
    if (size == 1) return listOf(this)
    return flatMap { element -> (this - element).permutations().map { it + element } }
}


fun <E> List<E>.pairs(): Sequence<Pair<E, E>> = combinations(2).map { it[0] to it[1] }

fun <E> List<E>.combinations(groupSize: Int): Sequence<List<E>> = sequence {
    check(groupSize in 1..size)

    slice(0..size - groupSize).mapIndexed { index, element ->
        if (groupSize == 1) {
            yield(listOf(element))
        } else {
            val subCombinations = subList(index + 1, size).combinations(groupSize - 1)
            subCombinations.forEach { yield(listOf(element) + it) }
        }
    }
}


fun <T1, T2> Pair<T1, T2>.flip(): Pair<T2, T1> = second to first
