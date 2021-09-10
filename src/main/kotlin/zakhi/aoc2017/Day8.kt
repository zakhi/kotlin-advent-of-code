package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf


fun main() {
    val instructions = matchEachLineOf(
        "aoc2017/day8",
        Regex("""(\w+) (inc|dec) (-?\d+) if (\w+) ([<>=!]+) (-?\d+)""")
    ) { (target, change, amount, source, operator, value) ->
        val changeAmount = amount.toInt() * (if (change == "dec") -1 else 1)
        ConditionalInstruction(target, changeAmount, source, operator, value.toInt())
    }

    val processor = ConditionalProcessor()
    processor.process(instructions)

    println("The largest value is ${processor.largestValue}")
    println("The largest value ever held is ${processor.largestValueEver}")
}


private class ConditionalProcessor {
    private val registers = mutableMapOf<String, Int>().withDefault { 0 }

    var largestValueEver: Int = 0
        private set

    val largestValue get() = registers.maxOf { it.value }

    fun process(instructions: List<ConditionalInstruction>) = instructions.forEach {
        if (it.conditionIsTrue) {
            val updatedValue = registers.getValue(it.targetRegister) + it.changeAmount

            registers[it.targetRegister] = updatedValue
            largestValueEver = maxOf(largestValueEver, updatedValue)
        }
    }

    private val ConditionalInstruction.conditionIsTrue: Boolean get() {
        val value = registers.getValue(sourceRegister)

        return when (operator) {
            "<" -> value < sourceValue
            ">" -> value > sourceValue
            "<=" -> value <= sourceValue
            ">=" -> value >= sourceValue
            "==" -> value == sourceValue
            "!=" -> value != sourceValue
            else -> throw Exception("Unknown conditional operator $operator")
        }
    }
}

private data class ConditionalInstruction(
    val targetRegister: String,
    val changeAmount: Int,
    val sourceRegister: String,
    val operator: String,
    val sourceValue: Int
)
