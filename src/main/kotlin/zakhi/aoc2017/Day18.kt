package zakhi.aoc2017

import zakhi.helpers.fail
import zakhi.helpers.matchEachLineOf
import java.util.*


fun main() {
    val instructions = matchEachLineOf("aoc2017/day18", Regex("""(\w+) (-?\w+)\s?(-?\w+)?""")) { (name, x, y) ->
        Instruction(name, x, y)
    }

    val program = SoundAssembly(instructions)
    program.run()

    println("The first recorded frequency is ${program.recoveredSound}")

    val queue0 = LinkedList<Long>()
    val queue1 = LinkedList<Long>()

    val programs = listOf(
        CommunicationAssembly(id = 0, instructions, incoming = queue0, outgoing = queue1),
        CommunicationAssembly(id = 1, instructions, incoming = queue1, outgoing = queue0)
    )

    while (programs.any { it.canRun }) {
        programs.forEach { it.run() }
    }

    println("The number of times program 1 sent a value is ${programs[1].numberOfValuesSent}")
}


private data class Instruction(
    val name: String,
    val x: String,
    val y: String
)

private abstract class Assembly(
    protected val program: List<Instruction>
) {
    protected val registers = mutableMapOf<String, Long>().withDefault { 0 }
    protected var position = 0

    open val canRun: Boolean get() = position in program.indices

    fun run() {
        while (canRun) {
            val (name, x, y) = program[position]
            var jump = 1

            when (name) {
                "snd" -> snd(valueOf(x))
                "rcv" -> rcv(x) || break
                "set" -> registers[x] = valueOf(y)
                "add" -> registers[x] = valueOf(x) + valueOf(y)
                "mul" -> registers[x] = valueOf(x) * valueOf(y)
                "mod" -> registers[x] = valueOf(x).mod(valueOf(y))
                "jgz" -> if (valueOf(x) > 0) jump = valueOf(y).toInt()
                else -> fail("Invalid instruction $name")
            }

            position += jump
        }
    }

    protected abstract fun snd(value: Long)
    protected abstract fun rcv(target: String): Boolean

    private fun valueOf(value: String): Long = value.toLongOrNull() ?: registers.getValue(value)
}


private class SoundAssembly(program: List<Instruction>) : Assembly(program) {
    var recoveredSound: Long? = null
        private set

    override fun snd(value: Long) {
        recoveredSound = value
    }

    override fun rcv(target: String): Boolean = false
}


private class CommunicationAssembly(
    id: Int,
    program: List<Instruction>,
    private val incoming: Queue<Long>,
    private val outgoing: Queue<Long>
) : Assembly(program) {
    init {
        registers["p"] = id.toLong()
    }

    private var waiting = false

    var numberOfValuesSent: Int = 0
        private set

    override val canRun: Boolean get() = super.canRun && (!waiting || incoming.isNotEmpty())

    override fun snd(value: Long) {
        outgoing.offer(value)
        numberOfValuesSent += 1
    }

    override fun rcv(target: String): Boolean {
        val value = incoming.poll()
        if (value != null) registers[target] = value
        waiting = value == null

        return !waiting
    }
}
