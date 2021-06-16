package zakhi.aoc2016

import zakhi.helpers.linesOf
import zakhi.helpers.mapDestructured


fun main() {
    val addresses = linesOf("aoc2016/day7")

    val tlsAddresses = addresses.count { address -> address.supportsTLS }
    println("The number of addresses supporting TLS is $tlsAddresses")

    val sslAddresses = addresses.count { address -> address.supportsSSL }
    println("The number of addresses supporting SSL is $sslAddresses")
}

private val String.supportsTLS: Boolean get() =
    hypernet.abbaSequences.isEmpty() && supernet.abbaSequences.isNotEmpty()

private val String.supportsSSL: Boolean get() = supernet.abaSequences.any {
    val inverse = "${it.drop(1)}${it[1]}"
    inverse in hypernet.abaSequences
}

private val String.supernet: String get() = split(Regex("""\[.*?]""")).joinToString(separator = "|")
private val String.hypernet: String get() = Regex("""\[(.*?)]""").findAll(this).map { it.value }.joinToString(separator = "|")

val String.abbaSequences: List<String> get() = Regex("""(?=(\w)(\w)\2\1)""").findAll(this)
    .filter { it.hasDistinctGroups }.mapDestructured { (a, b) -> "$a$b$b$a" }

val String.abaSequences: List<String> get() = Regex("""(?=(\w)(\w)\1)""").findAll(this)
    .filter { it.hasDistinctGroups }.mapDestructured { (a, b) -> "$a$b$a" }

private val MatchResult.hasDistinctGroups: Boolean get() = groupValues.drop(1).distinct().size > 1
