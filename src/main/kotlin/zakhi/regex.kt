package zakhi


@Suppress("ClassName")
object regex {

    fun <R> Sequence<MatchResult>.mapDestructured(transform: (MatchResult.Destructured) -> R): List<R> =
        map { transform(it.destructured) }.toList()
}
