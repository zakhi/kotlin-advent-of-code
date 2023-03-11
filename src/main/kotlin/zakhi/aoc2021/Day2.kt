package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val instructions = matchEachLineOf("aoc2021/day2", Regex("""(\w+) (\d+)""")) { (command, amount) ->
        command to amount.toInt()
    }

    val location = instructions.fold(0 to 0) { current, (command, amount) ->
        current + when (command) {
            "forward" -> 1 to 0
            "down" -> 0 to 1
            "up" -> 0 to -1
            else -> fail("Invalid command $command")
        } * amount
    }

    println("The location product is ${location.product}")

    val (properLocation, _) = instructions.fold((0 to 0) to 0) { (position, aim), (command, amount) ->
        when (command) {
            "forward" -> position + (amount to amount * aim) to aim
            "down" -> position to aim + amount
            "up" -> position to aim - amount
            else -> fail("Invalid command $command")
        }
    }

    println("The proper location product is ${properLocation.product}")
}


private val Point.product get() = x * y
