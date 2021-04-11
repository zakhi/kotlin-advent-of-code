package zakhi


fun <E> List<E>.permutations(): List<List<E>> {
    if (size == 1) return listOf(this)
    return flatMap { element -> (this - element).permutations().map { it + element } }
}


fun <E> List<E>.pairs(): List<Pair<E, E>> {
    return flatMapIndexed { index, element -> slice(index + 1..lastIndex).map { secondElement -> element to secondElement } }
}

fun <T1, T2> Pair<T1, T2>.flip(): Pair<T2, T1> = second to first
