package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf


fun main() {
    val triples = matchEachLineOf("aoc2016/day3", Regex("""\s*(\d+)\s+(\d+)\s+(\d+)""")) { (a, b, c) ->
        Triple(a.toInt(), b.toInt(), c.toInt())
    }

    val triangles = triples.count { it.isPossibleTriangle }
    println("There are $triangles possible triangles")

    val columnTriples = triples.chunked(3).flatMap { (a, b, c) ->
        listOf(
            Triple(a.first, b.first, c.first),
            Triple(a.second, b.second, c.second),
            Triple(a.third, b.third, c.third),
        )
    }

    val columnTriangles = columnTriples.count { it.isPossibleTriangle }
    println("There are $columnTriangles possible column triangles")

}


private val Triple<Int, Int, Int>.isPossibleTriangle: Boolean get() = with(toList()) {
    return all { side -> side * 2 < sum() }
}
