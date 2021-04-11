package zakhi.aoc2015

import zakhi.divisors
import zakhi.entireTextOf
import zakhi.naturalNumbers
import zakhi.sqrtFloor


fun main() {
    val leastAmountOfPresents = entireTextOf("aoc2015/day20").toInt()

    val houseNumber = naturalNumbers(from = (leastAmountOfPresents / 10).sqrtFloor()).first { house ->
        house.divisors().sum() * 10 >= leastAmountOfPresents
    }

    println("The lowest house number is $houseNumber")

    val secondHouseNumber = naturalNumbers(from = (leastAmountOfPresents / 11).sqrtFloor()).first { house ->
        house.divisors().filter { it * 50 >= house }.sum() * 11 >= leastAmountOfPresents
    }

    println("The second lowest house number is $secondHouseNumber")
}
