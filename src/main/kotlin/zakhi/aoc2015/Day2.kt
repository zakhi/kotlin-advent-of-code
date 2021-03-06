package zakhi.aoc2015

import zakhi.helpers.combinations
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.product


fun main() {
    val presents = matchEachLineOf("aoc2015/day2", regex = Regex("""(\d+)x(\d+)x(\d+)""")) { groups ->
        Present(groups.toList().map { it.toInt() })
    }

    val totalWrappingPaper = presents.sumOf { it.wrappingPaperSize }
    println("Total wrapping paper needed is $totalWrappingPaper square feet")

    val totalRibbonLength = presents.sumOf { it.ribbonLength }
    println("Total ribbon needed is $totalRibbonLength feet")
}


private class Present(private val dimensions: List<Int>) {

    val wrappingPaperSize: Int get() = 2 * sides.sum() + sides.first()
    val ribbonLength: Int get() = 2 * dimensions.sorted().take(2).sum() + volume

    private val sides = dimensions.combinations(2).map { it.product() }.sorted()
    private val volume = dimensions.product()
}
