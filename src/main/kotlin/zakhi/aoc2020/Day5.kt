package zakhi.aoc2020

import zakhi.helpers.charList
import zakhi.helpers.join
import zakhi.helpers.linesOf


fun main() {
    val seats = linesOf("aoc2020/day5").toList()

    val seatIds = seats.map { pass ->
        pass.charList().join { if (it in listOf('B', 'R')) "1" else "0" }.toInt(2)
    }

    println("The highest seat ID is ${seatIds.max()}")

    val mySeatId = (0..seatIds.max()).first { seat ->
        seat !in seatIds && seat - 1 in seatIds && seat + 1 in seatIds
    }

    println("The ID of the seat is $mySeatId")
}
