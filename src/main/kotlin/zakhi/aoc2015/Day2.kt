package zakhi.aoc2015

import zakhi.matchEachLineOf


fun main() {
    val presents = matchEachLineOf("aoc2015/day2", regex = Regex("""(\d+)x(\d+)x(\d+)""")) {
        Present(it.groupValues.drop(1).map(String::toInt))
    }

    val totalWrappingPaper = presents.sumBy(Present::wrappingPaperSize)
    println("Total wrapping paper needed is $totalWrappingPaper square feet")

    val totalRibbonLength = presents.sumBy(Present::ribbonLength)
    println("Total ribbon needed is $totalRibbonLength feet")
}


private class Present(private val dimensions: List<Int>) {

    val wrappingPaperSize: Int get() = 2 * sides.sum() + sides.first()
    val ribbonLength: Int get() = 2 * dimensions.sorted().take(2).sum() + volume

    private val sides = dimensions.flatMapIndexed { index, first -> dimensions.drop(index + 1).map { second -> first * second } }.sorted()
    private val volume = dimensions.reduce(Math::multiplyExact)
}
