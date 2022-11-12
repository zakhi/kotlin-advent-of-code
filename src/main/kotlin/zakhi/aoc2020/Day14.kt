package zakhi.aoc2020

import zakhi.helpers.join
import zakhi.helpers.linesOf
import kotlin.math.pow
import kotlin.properties.Delegates.notNull


fun main() {
    val input = linesOf("aoc2020/day14").toList()

    val system = BitmaskSystemV1()
    system.process(input)
    println("The sum of all memory entries is ${system.memorySum}")

    val systemV2 = BitmaskSystemV2()
    systemV2.process(input)
    println("The sum of all V2 memory entries is ${systemV2.memorySum}")
}


private abstract class BitmaskSystem {
    protected val memory = mutableMapOf<Long, Long>()

    val memorySum get() = memory.values.sum()

    fun process(program: List<String>) = program.forEach { instruction ->
        if (instruction.startsWith("mask")) {
            updateMasks(instruction.parseAsMask())
        } else {
            val (address, value) = instruction.parseAsMemorySet()
            setMemoryValue(address, value)
        }
    }

    protected abstract fun updateMasks(mask: String)
    protected abstract fun setMemoryValue(address: Long, value: Long)

    private fun String.parseAsMask(): String = Regex("""mask = ([01X]+)""").matchEntire(this)?.groupValues?.get(1) ?: throw Exception("Invalid mask in $this")

    private fun String.parseAsMemorySet(): List<Long> =
        Regex("""mem\[(\d+)] = (\d+)""").matchEntire(this)?.groupValues?.slice(1..2)?.map { it.toLong() } ?: throw Exception("Invalid instruction $this")
}


private class BitmaskSystemV1 : BitmaskSystem() {
    private var andMask by notNull<Long>()
    private var orMask by notNull<Long>()

    override fun updateMasks(mask: String) {
        andMask = mask.replace('X', '1').toLong(2)
        orMask = mask.replace('X', '0').toLong(2)
    }

    override fun setMemoryValue(address: Long, value: Long) {
        memory[address] = value and andMask or orMask
    }
}

private class BitmaskSystemV2 : BitmaskSystem() {
    private var baseMask by notNull<Long>()
    private lateinit var masks: List<Long>

    override fun updateMasks(mask: String) {
        baseMask = mask.map { if (it == 'X') '0' else '1' }.join().toLong(2)
        val degree = mask.count { it == 'X' }

        masks = (0 until 2.0.pow(degree).toLong()).map { i ->
            val pattern = i.toString(2).padStart(degree, '0').toMutableList()
            Regex("X").replace(mask) { "${pattern.removeFirst()}" }.toLong(2)
        }
    }

    override fun setMemoryValue(address: Long, value: Long) {
        masks.forEach { mask -> memory[address and baseMask or mask] = value }
    }
}
