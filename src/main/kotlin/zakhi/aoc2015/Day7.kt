package zakhi.aoc2015

import zakhi.matchEachLineOf


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

private val formulas = listOf(
    Formula(Regex("""(\d+)""")) { (number) -> number.toInt() },
    Formula(Regex("""(\w+)""")) { (wire) -> signalOf(wire) },
    Formula(Regex("""(\d+) AND (\w+)""")) { (number, wire) -> number.toInt() and signalOf(wire) },
    Formula(Regex("""(\w+) AND (\w+)""")) { (a, b) -> signalOf(a) and signalOf(b) },
    Formula(Regex("""(\w+) OR (\w+)""")) { (a, b) -> signalOf(a) or signalOf(b) },
    Formula(Regex("""NOT (\w+)""")) { (wire) -> signalOf(wire).inv() },
    Formula(Regex("""(\w+) LSHIFT (\d+)""")) { (wire, number) -> signalOf(wire) shl number.toInt() },
    Formula(Regex("""(\w+) RSHIFT (\d+)""")) { (wire, number) -> signalOf(wire) shr number.toInt() },
)

private val wireValues = mutableMapOf<String, Int>()

private fun signalOf(wire: String): Int = wireValues.computeIfAbsent(wire) {
    val instruction = instructions.getValue(wire)
    val formula = formulas.find { it.matches(instruction) } ?: throw Exception("cannot find formula for $instruction")

    formula.calculate(instruction)
}

private class Formula(
    private val regex: Regex,
    private val calculation: (MatchResult.Destructured) -> Int
) {

    fun matches(instruction: String): Boolean = regex.matches(instruction)

    fun calculate(instruction: String): Int {
        val match = regex.matchEntire(instruction) ?: throw Exception("formula '$instruction' does not match /$regex/")
        return calculation(match.destructured)
    }
}
