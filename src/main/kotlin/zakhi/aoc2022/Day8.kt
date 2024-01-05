package zakhi.aoc2022

import zakhi.helpers.*


fun main() {
    val trees = linesOf("aoc2022/day8").flatMapIndexed { y, line ->
        line.mapIndexed { x, char -> (x to y) to char.digitToInt() }
    }.toMap()

    val treeVisibilities = TreeVisibilities(trees)
    treeVisibilities.scan()

    println("The number of trees visible from outside is ${treeVisibilities.visibleFromOutside}")

    val highestScore = trees.keys.maxOf { treeVisibilities.scenicScoreOf(it) }
    println("The highest scenic score is $highestScore")
}


private class TreeVisibilities(private val trees: Map<Point, Int>) {
    private val max = trees.keys.maxOf { it.x }
    private val visibleTreesFromOutside = mutableSetOf<Point>()
    private val visibilities = listOf(
        TreeLineVisibilities { (x, y) -> x to y },
        TreeLineVisibilities { (x, y) -> max - x to y },
        TreeLineVisibilities { (x, y) -> y to x },
        TreeLineVisibilities { (x, y) -> y to max - x }
    )

    fun scan() {
        (0..max).forEach { y ->
            visibilities.forEach { sideVisibility ->
                val treeLine = TreeLine((0..max).asSequence().map { x -> trees.getValue(sideVisibility.transform(x to y)) })
                visibleTreesFromOutside.addAll(treeLine.visibleFromOutside.map { x -> sideVisibility.transform(x to y) })
                treeLine.visibilities.forEachIndexed { x, visibility -> sideVisibility[x to y] = visibility }
            }
        }
    }

    val visibleFromOutside get() = visibleTreesFromOutside.size

    fun scenicScoreOf(tree: Point) = visibilities.map { it[tree] }.reduce(Int::times)
}

private class TreeLine(heights: Sequence<Int>) {
    val visibilities = mutableListOf<Int>()
    val visibleFromOutside = mutableSetOf<Int>()

    private val stack = stackOf<IndexedTree>()

    init {
        heights.forEachIndexed { index, height ->
            val farthest = farthestVisibleTree(height)

            if (height > farthest.height) {
                visibleFromOutside.add(index)
            }

            stack.add(IndexedTree(index, height))
            visibilities.add(index - farthest.index)
        }
    }

    private fun farthestVisibleTree(height: Int): IndexedTree {
        while (true) {
            val previous = stack.head() ?: return IndexedTree(index = 0, height = -1)

            if (height >= previous.height) stack.pop()
            if (height <= previous.height) return previous
        }
    }
}

private class TreeLineVisibilities(private val transformation: (Point) -> Point) {
    val visibilities = mutableMapOf<Point, Int>()

    fun transform(point: Point) = transformation(point)

    operator fun set(point: Point, value: Int) {
        visibilities[transform(point)] = value
    }

    operator fun get(point: Point) = visibilities.getValue(point)
}

private data class IndexedTree(
    val index: Int,
    val height: Int
)
