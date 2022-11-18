package zakhi.helpers


fun entireTextOf(fileName: String): String = findResource(fileName).readText()

fun linesOf(fileName: String): Sequence<String> = findResource(fileName).readText().trimEnd().lineSequence()

fun <T> matchEntireTextOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): T {
    val match = regex.find(entireTextOf(fileName)) ?: fail("text of $fileName does not match /$regex/")
    return transform(match.destructured)
}

fun <T> findAllInEntireTextOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): List<T> {
    return regex.findAll(entireTextOf(fileName)).mapDestructured(transform)
}

fun <T> matchEachLineOf(fileName: String, regex: Regex, transform: (MatchResult.Destructured) -> T): List<T> =
    linesOf(fileName).mapIndexed { index, line ->
        regex.matchEntire(line) ?: fail("line ${index + 1} of $fileName does not match /$regex/")
    }.mapDestructured(transform)

private fun findResource(fileName: String) =
    object {}::class.java.getResource("/zakhi/$fileName") ?: fail("Could not find resource $fileName")
