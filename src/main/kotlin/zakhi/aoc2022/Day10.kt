package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val instructions = linesOf("aoc2022/day10").toList()

    val device = Device()
    instructions.forEach { device.run(it) }

    val sum = sequenceOf(20, 60, 100, 140, 180, 220).sumOf { device.signalAt(it) * it }
    println("The sum of the six signal strengths is $sum")

    val litPixels = grid(0..<40, 0..<6).filter { device.pixelLit(it) }.toList()

    println("The message is:")
    printPoints(litPixels)
}


private class Device {
    private val registerValues = mutableListOf(1)

    fun signalAt(cycle: Int) = registerValues[cycle]

    fun pixelLit(pixel: Point): Boolean {
        val cycle = pixel.y * 40 + pixel.x
        val signal = signalAt(cycle)
        val sprite = (signal - 1 .. signal + 1)

        return pixel.x in sprite
    }

    fun run(instruction: String) {
        tryMatch(instruction) {
            Regex("noop") to { increment() }
            Regex("""addx (-?\d+)""") to { (x) ->
                increment()
                increment(x.toInt())
            }
        }
    }

    private fun increment(value: Int = 0) {
        registerValues.add(registerValues.last() + value)
    }
}
