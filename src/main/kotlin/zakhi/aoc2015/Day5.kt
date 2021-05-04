package zakhi.aoc2015

import zakhi.input.linesOf


fun main() {
    val strings = linesOf("aoc2015/day5").toList()

    val niceStrings = strings.count { it.isNice }
    println("The number of nice strings is $niceStrings")

    val reallyNiceStrings = strings.count { it.isReallyNice }
    println("The number of really nice strings is $reallyNiceStrings")
}


private val String.isNice: Boolean get() =
    contains(Regex("([aeiou].*){3}")) &&
    contains(Regex("""(\w)\1""")) &&
    !contains(Regex("ab|cd|pq|xy"))

private val String.isReallyNice: Boolean get() =
    contains(Regex("""(\w\w).*\1""")) &&
    contains(Regex("""(\w).\1"""))
