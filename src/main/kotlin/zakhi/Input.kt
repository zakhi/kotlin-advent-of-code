package zakhi


fun entireTextOf(fileName: String): String =
    object {}::class.java.getResource("/zakhi/$fileName").readText()

fun linesOf(fileName: String): Sequence<String> =
    object {}::class.java.getResourceAsStream("/zakhi/$fileName").bufferedReader().lineSequence()

fun <T> matchEachLineOf(fileName: String, regex: Regex, transform: (MatchResult) -> T): List<T> =
    linesOf(fileName).mapIndexed { index, line ->
        val values = regex.matchEntire(line) ?: throw Exception("line ${index + 1} of $fileName does not match /$regex/")
        transform(values)
    }.toList()
