package zakhi.aoc2016

import zakhi.helpers.linesOf


fun main() {
    val rows = linesOf("aoc2016/day18").toMutableList()

    while (rows.size < 40) addNextRow(rows)
    println("The number of safe tiles in 40 rows is ${rows.safeTiles}")

    while (rows.size < 400000) addNextRow(rows)
    println("The number of safe tiles in 400,000 rows is ${rows.safeTiles}")
}


private val trapPatterns = listOf("^^.", ".^^", "..^", "^..")

private fun addNextRow(rows: MutableList<String>) {
    val previous = ".${rows.last()}."

    rows.add(buildString {
        rows.last().indices.forEach { i ->
            val tiles = previous.substring(i, i + 3)
            append(if (tiles in trapPatterns) "^" else ".")
        }
    })
}

private val MutableList<String>.safeTiles: Int get() = sumOf { row -> row.count { it == '.' } }
