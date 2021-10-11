package zakhi.aoc2018

import zakhi.helpers.entireTextOf


fun main() {
    val treeValues = Regex("""\d+""").findAll(entireTextOf("aoc2018/day8")).map { it.value.toInt() }.toList()

    println("The sum of all metadata is ${metadataSum(treeValues.toMutableList())}")
    println("The value of the root node is ${valueOf(treeValues.toMutableList())}")
}


private fun metadataSum(tree: MutableList<Int>): Int {
    val children = tree.removeFirst()
    val metadata = tree.removeFirst()

    return (1..children).sumOf { metadataSum(tree) } + (1..metadata).sumOf { tree.removeFirst() }
}

private fun valueOf(tree: MutableList<Int>): Int {
    val children = tree.removeFirst()
    val metadata = tree.removeFirst()

    val childValues = (1..children).associateWith { valueOf(tree) }.withDefault { 0 }
    val metadataValues = (1..metadata).map { tree.removeFirst() }

    return when (children) {
        0 -> metadataValues.sum()
        else -> metadataValues.sumOf { childValues.getValue(it) }
    }
}
