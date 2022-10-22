package zakhi.aoc2019

import zakhi.helpers.entireTextOf
import zakhi.helpers.second


fun main() {
    val program = readProgramFrom(entireTextOf("aoc2019/day23"))
    val dispatcher = Dispatcher(program)
    val result = dispatcher.run()
    println("The Y value of the first packet sent to 255 is $result")

    val natResult = Dispatcher(program, detectRepeatedIdleness = true).run()
    println("The Y value of packet delivered twice by the NAT is $natResult")
}


private class Dispatcher(
    program: List<Long>,
    private val detectRepeatedIdleness: Boolean = false
) {
    private val computers = (0..49).map { NetworkComputer(it, program) }
    private val nat = NAT()

    fun run(): Long {
        while (!terminated) {
            for (computer in computers) {
                computer.run()
            }
        }

        return nat.packet.second()
    }

    fun send(address: Int, values: List<Long>) {
        if (address == 255) {
            nat.accept(values)
        } else {
            computers[address].accept(values)
        }
    }

    private val terminated: Boolean get() {
        if (detectRepeatedIdleness) return nat.detectRepeatedIdleness()
        return nat.anyPacketReceived
    }

    private inner class NetworkComputer(
        address: Int,
        program: List<Long>
    ) {
        private val inputQueue = mutableListOf(address.toLong())
        private val outputQueue = mutableListOf<Long>()
        private val computer = IntcodeComputer(program, ::provideInput, ::consumeOutput, singleCommandMode = true)

        var idle: Boolean  = false
            private set

        fun run() {
            computer.start()
        }

        fun accept(values: List<Long>) {
            inputQueue.addAll(values)
            idle = false
        }

        fun provideInput(): Long {
            val value = inputQueue.removeFirstOrNull()
            idle = value == null
            return (value ?: -1).toLong()
        }

        fun consumeOutput(value: Long) {
            outputQueue.add(value)

            if (outputQueue.size == 3) {
                send(outputQueue.first().toInt(), outputQueue.drop(1))
                outputQueue.clear()
            }
        }
    }

    private inner class NAT {
        lateinit var packet: List<Long>
            private set

        private val sentHistory = LinkedHashSet<List<Long>>()

        val anyPacketReceived: Boolean get() = ::packet.isInitialized

        fun accept(packet: List<Long>) {
            this.packet = packet
        }

        fun detectRepeatedIdleness(): Boolean {
            if (!anyPacketReceived) return false

            if (computers.all { it.idle }) {
                send(0, packet)
                return !sentHistory.add(packet)
            }

            return false
        }
    }
}
