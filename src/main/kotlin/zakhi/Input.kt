package zakhi


fun entireTextOf(fileName: String): String = findResource(fileName).readText()

fun linesOf(fileName: String): Sequence<String> = findResource(fileName).readText().trim().lineSequence()

fun <T> matchEntireTextOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): T {
    val match = regex.find(entireTextOf(fileName)) ?: throw Exception("text of $fileName does not match /$regex/")
    return transform(match.destructured)
}

fun <T> findAllInEntireTextOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): List<T> {
    return regex.findAll(entireTextOf(fileName)).map { transform(it.destructured) }.toList()
}

fun <T> matchEachLineOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): List<T> =
    linesOf(fileName).mapIndexed { index, line ->
        val match = regex.matchEntire(line) ?: throw Exception("line ${index + 1} of $fileName does not match /$regex/")
        transform(match.destructured)
    }.toList()


private fun findResource(fileName: String) = object {}::class.java.getResource("/zakhi/$fileName") ?: throw Exception("Could not find resource $fileName")
