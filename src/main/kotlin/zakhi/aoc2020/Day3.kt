package zakhi.aoc2020

import zakhi.helpers.linesOf


fun main() {
    val input = linesOf("aoc2020/day3").toList()
    val right3Down1Trees = countTrees(input, right = 3, down = 1)
    println("The number of trees encountered is $right3Down1Trees")

    val treesMultiplied = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
        .map { (right, down) -> countTrees(input, right, down) }
        .reduce(UInt::times)

    println("The number of tree multiplied is $treesMultiplied")
}


private fun countTrees(map: List<String>, right: Int, down: Int): UInt =
    map.indices.step(down).map { map[it] }.withIndex()
        .count { (i, line) -> line[(i * right) % line.length] == '#' }.toUInt()
