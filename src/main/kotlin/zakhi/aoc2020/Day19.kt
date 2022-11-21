package zakhi.aoc2020

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail


fun main() {
    val (rulesText, messagesText) = entireTextOf("aoc2020/day19").trim().split("\n\n")
    val rules = parseGrammarRules(rulesText.lines())
    val messages = messagesText.lines()

    val validate = earleyParserOf(rules)
    val validMessages = messages.count { validate(it) }
    println("The number of valid messages is $validMessages")

    val updatedRules = rules.filterNot { it.id in listOf(8, 11) } + parseGrammarRules(listOf("8: 42 | 42 8", "11: 42 31 | 42 11 31"))

    val updatedValidate = earleyParserOf(updatedRules)
    val updatedValidMessages = messages.count { updatedValidate(it) }
    println("The number of valid messages after rule update is $updatedValidMessages")
}


private fun parseGrammarRules(lines: List<String>): List<GrammarRule> = lines.flatMap { line ->
    val (id, body) = Regex("""(\d+): (.+)""").matchEntire(line)?.destructured ?: fail("Invalid rule $line")
    parseTerminalSymbol(id.toInt(), body) ?: parseNonTerminalSymbols(id.toInt(), body)
}

private fun parseTerminalSymbol(id: Int, body: String): List<GrammarRule>? =
    Regex(""""(\w)"""").matchEntire(body)?.let { TerminalSymbol(it.groupValues[1].first()) }?.let { listOf(GrammarRule(id, listOf(it))) }

private fun parseNonTerminalSymbols(id: Int, body: String): List<GrammarRule> {
    return body.split("|").map { sequence ->
        val symbols = Regex("""\d+""").findAll(sequence).map { NonTerminalSymbol(it.value.toInt()) }.toList()
        GrammarRule(id, symbols)
    }
}

private interface Symbol
private data class TerminalSymbol(val char: Char) : Symbol
private data class NonTerminalSymbol(val id: Int) : Symbol

private data class GrammarRule(
    val id: Int,
    val symbols: List<Symbol>
)

private fun earleyParserOf(rules: List<GrammarRule>): (String) -> Boolean = { message -> EarleyParser(rules, message)() }

private class EarleyParser(
    private val rules: List<GrammarRule>,
    private val message: String
) {
    val stateSets = (0..message.length).map { mutableSetOf<EarleyItem>() }

    operator fun invoke(): Boolean {
        setStartRule()

        stateSets.indices.forEach { index ->
            val items = stateSets[index]
            val visited = mutableSetOf<EarleyItem>()

            while (!visited.containsAll(items)) {
                val item = items.first { it !in visited }

                when (val nextSymbol = item.nextSymbol) {
                    is TerminalSymbol -> scan(item, nextSymbol, index)
                    is NonTerminalSymbol -> predict(nextSymbol, index)
                    null -> complete(item, index)
                }

                visited.add(item)
            }
        }

        return stateSets.last().any { it.completeMatch }
    }

    private fun setStartRule() {
        rules.filter { it.id == 0 }.forEach { rule -> stateSets[0].add(EarleyItem(rule, 0, 0)) }
    }

    private fun scan(item: EarleyItem, nextSymbol: TerminalSymbol, index: Int) {
        if (message.getOrNull(index) == nextSymbol.char) {
            stateSets[index + 1].add(item.copy(next = item.next + 1))
        }
    }

    private fun predict(symbol: NonTerminalSymbol, index: Int) {
        rules.filter { it.id == symbol.id }.forEach { rule ->
            stateSets[index].add(EarleyItem(rule, index, 0))
        }
    }

    private fun complete(item: EarleyItem, index: Int) {
        stateSets[item.start].filter { oldItem -> (oldItem.nextSymbol as? NonTerminalSymbol)?.id == item.rule.id }.forEach { oldItem ->
            stateSets[index].add(oldItem.copy(next = oldItem.next + 1))
        }
    }
}

private data class EarleyItem(
    val rule: GrammarRule,
    val start: Int,
    val next: Int
) {
    val nextSymbol: Symbol? get() = rule.symbols.getOrNull(next)
    val completeMatch: Boolean get() = rule.id == 0 && start == 0 && nextSymbol == null
}
