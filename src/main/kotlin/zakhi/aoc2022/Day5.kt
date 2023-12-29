package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val (setup, instructionText) = entireTextOf("aoc2022/day5").trim().split("\n\n")
    val instructions = instructionText.lines().map {
        val (amount, from, to) = Regex("""move (\d+) from (\d) to (\d)""").matchEntire(it)?.destructured ?: fail("Invalid instruction: $it")
        CraneInstruction(amount.toInt(), from.toInt(), to.toInt())
    }

    val stacks = CraneStacks(setup)
    stacks.moveOneCrateAtATime(instructions)
    println("The top crates when moving one crate at a time are ${stacks.topCrates}")

    val newStacks = CraneStacks(setup)
    newStacks.moveMultipleCreates(instructions)
    println("The top crates when moving multiple crates are ${newStacks.topCrates}")
}


private class CraneStacks(setup: String) {
    val stacks = (1..9).associateWith { mutableListOf<Char>() }.also { stacks ->
        setup.lines().dropLast(1).map { line ->
            line.chunked(4).forEachIndexed { index, chunk ->
                Regex("""\[(\w)]""").find(chunk)?.let { stacks.getValue(index + 1).add(0, it.groupValues[1].first()) }
            }
        }
    }

    val topCrates get() = stacks.values.join { it.last().toString() }

    fun moveOneCrateAtATime(instructions: List<CraneInstruction>) {
        instructions.forEach { instruction ->
            repeat(instruction.amount) {
                val crate = stacks.getValue(instruction.from).pop()
                stacks.getValue(instruction.to).push(crate)
            }
        }
    }

    fun moveMultipleCreates(instructions: List<CraneInstruction>) {
        instructions.forEach { instruction ->
            val crates = stacks.getValue(instruction.from).pop(instruction.amount).reversed()
            stacks.getValue(instruction.to).addAll(crates)
        }
    }
}

private data class CraneInstruction(
    val amount: Int,
    val from: Int,
    val to: Int
)
