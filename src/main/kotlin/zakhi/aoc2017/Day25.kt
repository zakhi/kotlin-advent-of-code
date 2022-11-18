package zakhi.aoc2017

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail
import zakhi.helpers.mapDestructured


fun main() {
    val input = entireTextOf("aoc2017/day25").split("\n\n")
    val (startState, steps) = parseStartStateAndSteps(input.first())
    val states = input.drop(1).map { parseState(it) }

    val machine = TuringMachine(states, startState)
    repeat(steps) { machine.step() }

    println("The machine checksum is ${machine.checksum}")
}


private fun parseStartStateAndSteps(input: String): Pair<String, Int> {
    val state = Regex("""Begin in state (\w).""").findSingleGroup(input)
    val steps = Regex("""Perform a diagnostic checksum after (\d+) steps.""").findSingleGroup(input).toInt()

    return state to steps
}

private fun parseState(description: String): State {
    val stateId = Regex("""In state (\w):""").findSingleGroup(description)

    val actions = Regex("""If the current value is (\d):
            |\s+- Write the value (\d).
            |\s+- Move one slot to the (right|left).
            |\s+- Continue with state (\w).""".trimMargin()).findAll(description).mapDestructured { (value, writeValue, move, nextState) ->
        value.toInt() to StateAction(writeValue.toInt(), if (move == "right") 1 else -1, nextState)
    }.toMap()

    return State(stateId, actions)
}

private class TuringMachine(
    states: List<State>,
    startState: String
) {
    private val tape = mutableMapOf<Int, Int>().withDefault { 0 }
    private val states = states.associateBy { it.id }
    private var currentState = startState
    private var position = 0

    val checksum: Int get() = tape.values.count { it == 1 }

    fun step() {
        val value = tape.getValue(position)
        val action = states.getValue(currentState).actions.getValue(value)

        tape[position] = action.write
        position += action.move
        currentState = action.next
    }
}

private data class State(
    val id: String,
    val actions: Map<Int, StateAction>
)

private data class StateAction(
    val write: Int,
    val move: Int,
    val next: String
)

private fun Regex.findSingleGroup(input: String): String =
    find(input)?.groupValues?.get(1) ?: fail("Cannot find match in $input")
