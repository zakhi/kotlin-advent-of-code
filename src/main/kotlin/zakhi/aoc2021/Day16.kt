package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val transmission = Transmission(entireTextOf("aoc2021/day16").trim()
        .map { it.digitToInt(16) }.join { it.toString(2).padStart(4, '0') }.toList()
    )

    val packet = createPacket(transmission)

    println("The sum of all packet versions is ${packet.versions().sum()}")
    println("The value in the transmission is ${packet.value}")
}


private abstract class Packet(
    val version: Int,
    val typeId: Int
) {
    open fun versions() = listOf(version)

    abstract val value: Long
}

private class LiteralValuePacket(transmission: Transmission, version: Int, typeId: Int) : Packet(version, typeId) {
    override val value = sequence {
        while (true) {
            val lastGroup = transmission.next() == 0
            yield(transmission.next(4).toLong())
            if (lastGroup) break
        }
    }.reduce { current, newBits -> current.shl(4) + newBits }
}

private class OperatorPacket(transmission: Transmission, version: Int, typeId: Int) : Packet(version, typeId) {
    private val subPackets = when (val lengthType = transmission.next()) {
        0 -> subPacketsByLength(transmission)
        else -> subPacketsByCount(transmission)
    }

    override fun versions(): List<Int> = super.versions() + subPackets.flatMap { it.versions() }

    override val value: Long get() = when (typeId) {
        0 -> subPackets.sumOf { it.value }
        1 -> subPackets.map { it.value }.product()
        2 -> subPackets.minOf { it.value }
        3 -> subPackets.maxOf { it.value }
        5 -> if (subPackets.first().value > subPackets.second().value) 1 else 0
        6 -> if (subPackets.first().value < subPackets.second().value) 1 else 0
        7 -> if (subPackets.first().value == subPackets.second().value) 1 else 0
        else -> fail("Invalid type ID $typeId")
    }

    private fun subPacketsByLength(transmission: Transmission): List<Packet> = sequence {
        val length = transmission.next(15)

        val subTransmission = transmission.subTransmission(length)
        while (!subTransmission.isEmpty()) yield(createPacket(subTransmission))
    }.toList()

    private fun subPacketsByCount(transmission: Transmission): List<Packet> =
        (1..transmission.next(11)).map { createPacket(transmission) }
}

private fun createPacket(transmission: Transmission): Packet {
    val version = transmission.next(3)

    return when (val typeId = transmission.next(3)) {
        4 -> LiteralValuePacket(transmission, version, typeId)
        else -> OperatorPacket(transmission, version, typeId)
    }
}

private class Transmission(binaryDigits: List<Char>) {
    private val queue = binaryDigits.toMutableList()

    fun next(): Int = queue.removeFirst().digitToInt(2)
    fun next(bits: Int): Int = queue.removeFirst(bits).join().toInt(2)

    fun isEmpty(): Boolean = queue.isEmpty()
    fun subTransmission(length: Int): Transmission = Transmission(queue.removeFirst(length))

    private fun MutableList<Char>.removeFirst(bits: Int): List<Char> = (1..bits).map { removeFirst() }
}
