package zakhi.aoc2016

import zakhi.helpers.linesOf


fun main() {
    val instructions = linesOf("aoc2016/day10").toList()

    val bots = instructions.mapNotNull { line ->
        Regex("""bot (\d+) gives low to (bot|output) (\d+) and high to (bot|output) (\d+)""").matchEntire(line)?.destructured?.let { (id, lowTarget, lowId, highTarget, highId) ->
            Bot(id.toInt(), lowTarget to lowId.toInt(), highTarget to highId.toInt())
        }
    }.let { Bots(it) }

    instructions.forEach { line ->
        Regex("""value (\d+) goes to bot (\d+)""").matchEntire(line)?.destructured?.let { (value, id) ->
            bots.receive(id.toInt(), value.toInt())
        }
    }

    val outputs = Outputs()

    while (!outputs.filled) {
        bots.forEach { bot -> bot.give(bots, outputs) }
    }

    val targetBot = bots.botComparing(17, 61)

    println("The bot number is ${targetBot.id}")
    println("The product of outputs 0, 1, and 2 is ${outputs.product}")
}


private class Outputs {
    private val values = mutableMapOf<Int, Int>().withDefault { 0 }

    fun receive(output: Int, value: Int) {
        values[output] = value
    }

    val filled: Boolean get() = product > 0
    val product: Int get() = (0..2).map { values.getValue(it) }.reduce(Int::times)
}

private class Bots(bots: List<Bot>) {
    private val botsById = bots.associateBy { it.id }

    fun receive(botId: Int, value: Int) {
        botsById.getValue(botId).receive(value)
    }

    fun forEach(botConsumer: (Bot) -> Unit) = botsById.values.forEach(botConsumer)

    fun botComparing(first: Int, second: Int): Bot = botsById.values.first { it.compares(first, second) }
}

private class Bot(
    val id: Int,
    private val lowInstruction: Pair<String, Int>,
    private val highInstruction: Pair<String, Int>
) {
    private val values = mutableSetOf<Int>()
    private var given = false

    fun receive(value: Int) {
        values.add(value)
    }

    fun give(bots: Bots, outputs: Outputs) {
        if (values.size == 2 && !given) {
            val (low, high) = values.sorted()
            giveChip(low, lowInstruction, bots, outputs)
            giveChip(high, highInstruction, bots, outputs)
            given = true
        }
    }

    fun compares(first: Int, second: Int): Boolean = values == setOf(first, second)

    private fun giveChip(value: Int, instruction: Pair<String, Int>, bots: Bots, outputs: Outputs) {
        val (target, id) = instruction
        when (target) {
            "output" -> outputs.receive(id, value)
            "bot" -> bots.receive(id, value)
        }
    }
}
