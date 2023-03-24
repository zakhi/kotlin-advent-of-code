package zakhi.aoc2021

import zakhi.helpers.*


fun main() {
    val entries = matchEachLineOf("aoc2021/day8", Regex("""(.+) \| (.+)""")) { (input, output) ->
        Entry(input.toCharSets(), output.toCharSets())
    }

    val oneFourSevenEightCount = entries.flatMap { it.output }.count { it.size in listOf(2, 3, 4, 7) }
    println("The number of appearances of 1, 4, 7, or 8 in the output is $oneFourSevenEightCount")

    val outputValueSum = entries.sumOf { it.outputValue() }
    println("The output values sum is $outputValueSum")
}


private data class Entry(
    val input: List<Set<Char>>,
    val output: List<Set<Char>>
) {
    fun outputValue(): Int {
        val matches = mutableMapOf<Int, Set<Char>>()

        matches[1] = input.first { it.size == 2 }
        matches[4] = input.first { it.size == 4 }
        matches[7] = input.first { it.size == 3 }
        matches[8] = input.first { it.size == 7 }
        matches[6] = input.first { it.size == 6 && (it intersect matches.getValue(1)).size == 1 }
        matches[9] = input.first { it.size == 6 && (it intersect matches.getValue(4)).size == 4 }
        matches[0] = input.first { it.size == 6 && it != matches[6] && it != matches[9] }
        matches[3] = input.first { it.size == 5 && (it - matches.getValue(1)).size == 3 }
        matches[5] = input.first { it.size == 5 && (matches.getValue(6) - it).size == 1 }
        matches[2] = input.first { it.size == 5 && it != matches[3] && it != matches[5] }

        return output.join { matches.firstKeyByValue(it).toString() }.toInt()
    }
}

private fun String.toCharSets() = split(" ").map { it.charList().toSet() }
