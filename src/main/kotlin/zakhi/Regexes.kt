package zakhi

object Regexes {

    fun <R> Sequence<MatchResult>.mapDestructured(transform: (MatchResult.Destructured) -> R): List<R> =
        map { transform(it.destructured) }.toList()
}
