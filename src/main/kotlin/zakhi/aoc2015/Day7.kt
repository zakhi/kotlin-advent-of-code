package zakhi.aoc2015

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.tryMatch


fun main() {
    val aSignal = signalOf("a")
    println("The signal to wire a is $aSignal")

    wireValues.clear()
    wireValues["b"] = aSignal

    println("The new signal to wire a is ${signalOf("a")}")
}


private val instructions = matchEachLineOf("aoc2015/day7", Regex("""(.*) -> (\w+)""")) { (instruction, targetWire) ->
    targetWire to instruction
}.toMap()

private val wireValues = mutableMapOf<String, Int>()

private fun signalOf(wire: String): Int {
    if (wire in wireValues) return wireValues.getValue(wire)

    val instruction = instructions.getValue(wire)

    val value = tryMatch<Int>(instruction) {
        Regex("""(\d+)""") to { (number) -> number.toInt() }
        Regex("""(\w+)""") to { (wire) -> signalOf(wire) }
        Regex("""(\d+) AND (\w+)""") to { (number, wire) -> number.toInt() and signalOf(wire) }
        Regex("""(\w+) AND (\w+)""") to { (a, b) -> signalOf(a) and signalOf(b) }
        Regex("""(\w+) OR (\w+)""") to { (a, b) -> signalOf(a) or signalOf(b) }
        Regex("""NOT (\w+)""") to { (wire) -> signalOf(wire).inv() }
        Regex("""(\w+) LSHIFT (\d+)""") to { (wire, number) -> signalOf(wire) shl number.toInt() }
        Regex("""(\w+) RSHIFT (\d+)""") to { (wire, number) -> signalOf(wire) shr number.toInt() }
    } ?: throw Exception("cannot find formula for $instruction")

    wireValues[wire] = value
    return value
}
