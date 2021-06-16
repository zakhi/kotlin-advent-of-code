package zakhi.aoc2015

import zakhi.helpers.permutations
import zakhi.helpers.matchEachLineOf


fun main() {
    val cities = distances.keys.flatten().distinct()
    val routes = cities.permutations()

    val shortestDistance = routes.minOf { distanceOf(it) }
    println("The shortest route distance is $shortestDistance")

    val longestDistance = routes.maxOf { distanceOf(it) }
    println("The longest route distance is $longestDistance")
}


private val distances = matchEachLineOf("aoc2015/day9", Regex("""(\w+) to (\w+) = (\d+)""")) { (start, end, distance) ->
    setOf(start, end) to distance.toInt()
}.toMap()

private fun distanceOf(route: List<String>): Int = route.zipWithNext().sumOf { (start, end) ->
    distances.getValue(setOf(start, end))
}
