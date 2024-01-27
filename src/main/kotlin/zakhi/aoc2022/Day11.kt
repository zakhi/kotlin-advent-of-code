package zakhi.aoc2022

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail
import zakhi.helpers.product
import java.math.BigInteger


fun main() {
    val setup = entireTextOf("aoc2022/day11").trim().split("\n\n").map { parseMonkey(it.lines()) }
    val modulo = setup.map { it.divisibleBy }.product()

    val monkeys = setup.map { Monkey(it, modulo, worryLevel = 3) }
    repeat(20) { playRound(monkeys) }

    val monkeyBusinessLevel = monkeys.map { it.inspections }.sortedDescending().take(2).product()
    println("The level of monkey business is $monkeyBusinessLevel")

    val noWorryMonkeys = setup.map { Monkey(it, modulo, worryLevel = 1) }
    repeat(10000) { playRound(noWorryMonkeys) }

    val noWorryMonkeyBusinessLevel = noWorryMonkeys.map { it.inspections.toLong() }.sortedDescending().take(2).product()
    println("The level of monkey business with no worries is $noWorryMonkeyBusinessLevel")
}


private typealias Operation = (Int, Int) -> Int

private fun parseMonkey(lines: List<String>): MonkeySetup {
    val items = Regex("""\d+""").findAll(lines[1]).map { it.value.toInt() }.toList()
    val operation = Regex("""new = old ([*+]) (.+)""").find(lines[2])?.destructured?.let { (symbol, other) -> parseOperation(symbol, other) } ?: fail("Unknown operation")
    val divisibleBy = parseNumber(lines[3])
    val throwToIfTrue = parseNumber(lines[4])
    val throwToIfFalse = parseNumber(lines[5])

    return MonkeySetup(items, operation, divisibleBy, throwToIfTrue, throwToIfFalse)
}

private fun parseNumber(line: String) =
    Regex("""\d+""").find(line)?.value?.toInt() ?: fail("Unknown number at $line")

private fun parseOperation(symbol: String, other: String): Operation = when (symbol) {
    "+" -> { item, mod -> (item + other.toInt()).mod(mod) }
    "*" -> when (other) {
        "old" -> { item, mod -> item.toBigInteger().modPow(BigInteger.TWO, mod.toBigInteger()).toInt() }
        else -> { item, mod -> (item * other.toInt()).mod(mod) }
    }

    else -> fail("Unknown operation $symbol $other")
}

private fun playRound(monkeys: List<Monkey>) {
    monkeys.forEach { monkey ->
        while (monkey.hasItems) {
            val (item, index) = monkey.throwNextItem()
            monkeys[index].receiveItem(item)
        }
    }
}

private data class MonkeySetup(
    val items: List<Int>,
    val operation: Operation,
    val divisibleBy: Int,
    val throwToIfTrue: Int,
    val throwToIfFalse: Int
) {
    fun evaluate(item: Int, worryLevel: Int, mod: Int): Pair<Int, Int> {
        val newItemValue = (operation(item, mod) / worryLevel).mod(mod)
        return newItemValue to if (newItemValue % divisibleBy == 0) throwToIfTrue else throwToIfFalse
    }
}

private class Monkey(
    private val setup: MonkeySetup,
    private val modulo: Int,
    private val worryLevel: Int
) {
    private val items = setup.items.toMutableList()

    var inspections = 0
        private set

    val hasItems get() = items.size > 0

    fun throwNextItem(): Pair<Int, Int> {
        inspections += 1
        return setup.evaluate(items.removeFirst(), worryLevel, modulo)
    }

    fun receiveItem(item: Int) {
        items.add(item)
    }
}
