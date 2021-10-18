package zakhi.helpers


fun <E> List<E>.permutations(): List<List<E>> {
    if (size == 1) return listOf(this)
    return flatMap { element -> (this - element).permutations().map { it + element } }
}

fun <E> List<E>.pairs(): Sequence<Pair<E, E>> = combinations(2).map { it[0] to it[1] }

fun <E> List<E>.orderedPairs(): Sequence<Pair<E, E>> = product(this).filterNot { (a, b) -> a == b }

fun <E> List<E>.combinations(groupSize: Int): Sequence<List<E>> = sequence {
    if (groupSize !in 1..size) return@sequence

    slice(0..size - groupSize).mapIndexed { index, element ->
        if (groupSize == 1) {
            yield(listOf(element))
        } else {
            val subCombinations = subList(index + 1, size).combinations(groupSize - 1)
            subCombinations.forEach { yield(listOf(element) + it) }
        }
    }
}

fun <T, U> List<T>.product(other: List<U>): Sequence<Pair<T, U>> = sequence {
    for (first in iterator()) {
        for (second in other) {
            yield(first to second)
        }
    }
}

fun <E> List<E>.cyclicNextFrom(index: Int): E = get((index + 1).mod(size))
fun <E> List<E>.cyclicPreviousFrom(index: Int): E = get((index - 1).mod(size))

fun <E> List<E>.second() = get(1)

fun <T1, T2> Pair<T1, T2>.flip(): Pair<T2, T1> = second to first

inline fun <T, R : Comparable<R>> Iterable<T>.maxBy(selector: (T) -> R) = maxByOrNull(selector) ?: throw NoSuchElementException()
inline fun <T, R : Comparable<R>> Sequence<T>.maxBy(selector: (T) -> R) = maxByOrNull(selector) ?: throw NoSuchElementException()

inline fun <T, R : Comparable<R>> Iterable<T>.minBy(selector: (T) -> R) = minByOrNull(selector) ?: throw NoSuchElementException()
inline fun <T, R : Comparable<R>> Sequence<T>.minBy(selector: (T) -> R) = minByOrNull(selector) ?: throw NoSuchElementException()

fun <T : Comparable<T>> Iterable<T>.max(): T = maxOrNull() ?: throw NoSuchElementException()
fun <T : Comparable<T>> Iterable<T>.min(): T = minOrNull() ?: throw NoSuchElementException()
