package zakhi.aoc2017

import zakhi.helpers.entireTextOf
import java.util.*


fun main() {
    val steps = entireTextOf("aoc2017/day17").trim().toInt()

    println("The value after last added is ${findSpinlockValueAfter2017(steps)}")
    println("The value after 0 is ${findSpinlockSecondValue(steps)}")
}


private fun findSpinlockValueAfter2017(steps: Int): Int {
    val spinlock = LinkedList(listOf(0))
    var position = 0

    (1..2017).forEach { i ->
        position = (position + steps).mod(i) + 1
        spinlock.add(position, i)
    }

    return spinlock[(spinlock.indexOf(2017) + 1).mod(spinlock.size)]
}

private fun findSpinlockSecondValue(steps: Int): Int {
    var position = 0
    var valueAfterZero = 1

    (1..50_000_000).forEach { i ->
        position = (position + steps).mod(i) + 1
        if (position == 1) valueAfterZero = i
    }

    return valueAfterZero
}
