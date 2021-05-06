package zakhi.aoc2015

import zakhi.input.entireTextOf
import zakhi.numbers.divisors
import zakhi.numbers.floorSqrt
import zakhi.numbers.wholeNumbers


fun main() {
    val leastAmountOfPresents = entireTextOf("aoc2015/day20").toInt()

    val houseNumber = wholeNumbers(from = (leastAmountOfPresents / 10).floorSqrt()).first { house ->
        house.divisors().sum() * 10 >= leastAmountOfPresents
    }

    println("The lowest house number is $houseNumber")

    val secondHouseNumber = wholeNumbers(from = (leastAmountOfPresents / 11).floorSqrt()).first { house ->
        house.divisors().filter { it * 50 >= house }.sum() * 11 >= leastAmountOfPresents
    }

    println("The second lowest house number is $secondHouseNumber")
}
