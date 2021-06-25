package zakhi.helpers


fun <R> Sequence<MatchResult>.mapDestructured(transform: (MatchResult.Destructured) -> R): List<R> =
    map { transform(it.destructured) }.toList()

inline fun <T> tryMatch(value: String, block: RegexMatcher<T>.() -> Unit): T? = RegexMatcher<T>().apply(block).match(value)

@DslMarker
annotation class RegexDsl

@RegexDsl
class RegexMatcher<T> {
    private val patterns = mutableMapOf<Regex, (MatchResult.Destructured) -> T>()

    infix fun Regex.to(block: (MatchResult.Destructured) -> T) {
        patterns[this] = block
    }

    fun match(value: String): T? =
        patterns.firstNotNullOfOrNull { (regex, operation) -> regex.matchEntire(value)?.destructured?.let(operation) }
}
