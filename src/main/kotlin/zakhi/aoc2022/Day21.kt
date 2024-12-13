package zakhi.aoc2022

import zakhi.helpers.fail
import zakhi.helpers.matchEachLineOf


fun main() {
    val monkeys = mutableMapOf<String, YellingMonkey>()

    matchEachLineOf("aoc2022/day21", Regex("""(\w+): (\w+ [+\-*/] \w+|\d+)""")) { (name, job) ->
        monkeys.put(name, parseMonkey(name, job, monkeys))
    }

    val rootMonkey = monkeys.getValue("root")
    println("The root monkey yells ${rootMonkey.number}")

    val references = monkeys.flatMap { (name, monkey) ->
        when (monkey) {
            is NumberMonkey -> emptyList()
            is OperationMonkey -> listOf(monkey.left to name, monkey.right to name)
        }
    }.toMap()

    val humanNumber = monkeys.getValue(references.getValue("humn")).expectedNumberOf("humn", references)
    println("The human should yell $humanNumber")
}

private fun parseMonkey(name: String, job: String, monkeys: Map<String, YellingMonkey>): YellingMonkey {
    val number = job.toLongOrNull()
    if (number != null) return NumberMonkey(name, number)

    val (leftName, operation, rightName) = job.split(" ")
    return OperationMonkey(name, leftName, rightName, operation, monkeys)
}

private sealed interface YellingMonkey {
    val name: String
    val number: Long

    fun expectedNumberOf(other: String, references: Map<String, String>): Long = fail()
}

private data class NumberMonkey(
    override val name: String,
    override val number: Long
) : YellingMonkey

private class OperationMonkey(
    override val name: String,
    val left: String,
    val right: String,
    private val operation: String,
    private val monkeys: Map<String, YellingMonkey>
) : YellingMonkey {

    override val number: Long by lazy {
        when (operation) {
            "+" -> leftNumber() + rightNumber()
            "-" -> leftNumber() - rightNumber()
            "*" -> leftNumber() * rightNumber()
            "/" -> leftNumber() / rightNumber()
            else -> fail("Unknown operation $operation")
        }
    }

    override fun expectedNumberOf(other: String, references: Map<String, String>): Long {
        if (!references.contains(name)) {
            return if (other == left) rightNumber() else leftNumber()
        }

        val myExpectedNumber = monkeys.getValue(references.getValue(name)).expectedNumberOf(name, references)

        return when (operation) {
            "+" -> if (other == left) myExpectedNumber - rightNumber() else myExpectedNumber - leftNumber()
            "-" -> if (other == left) myExpectedNumber + rightNumber() else leftNumber() - myExpectedNumber
            "*" -> if (other == left) myExpectedNumber / rightNumber() else myExpectedNumber / leftNumber()
            "/" -> if (other == left) myExpectedNumber * rightNumber() else leftNumber() / myExpectedNumber
            else -> fail("Unknown operation $operation")
        }
    }

    private fun rightNumber() = monkeys.getValue(right).number
    private fun leftNumber() = monkeys.getValue(left).number
}
