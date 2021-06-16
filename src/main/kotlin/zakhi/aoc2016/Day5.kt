package zakhi.aoc2016

import zakhi.helpers.entireTextOf
import zakhi.helpers.wholeNumbers
import zakhi.helpers.join
import zakhi.helpers.md5Hash


fun main() {
    val doorId = entireTextOf("aoc2016/day5")

    val password = wholeNumbers(from = 0).mapNotNull { index ->
        "$doorId$index".md5HashStartingWithFiveZeros()?.get(5)
    }.take(8).join()

    println("The password is $password")

    val secondPasswordChars = arrayOfNulls<Char>(8)

    wholeNumbers(from = 0).takeWhile { null in secondPasswordChars }.forEach { index ->
        val hash = "$doorId$index".md5HashStartingWithFiveZeros()
        val position = hash?.get(5)?.digitToIntOrNull()?.takeIf { it in secondPasswordChars.indices }

        if (position != null) secondPasswordChars[position] = secondPasswordChars[position] ?: hash[6]
    }

    println("The second password is ${secondPasswordChars.join()}")
}


private fun String.md5HashStartingWithFiveZeros() = md5Hash().takeIf { hash -> hash.startsWith("00000") }
