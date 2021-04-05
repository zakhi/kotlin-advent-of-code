package zakhi.aoc2015

import zakhi.matchEachLineOf

fun main() {
    val cities = distances.keys.flatten().distinct()
    val routes = allRoutesBetween(cities)

    val shortestDistance = routes.minOf { distanceOf(it) }
    println("The shortest route distance is $shortestDistance")

    val longestDistance = routes.maxOf { distanceOf(it) }
    println("The longest route distance is $longestDistance")
}


private val distances = matchEachLineOf("aoc2015/day9", Regex("""(\w+) to (\w+) = (\d+)""")) { (start, end, distance) ->
    setOf(start, end) to distance.toInt()
}.toMap()

private fun allRoutesBetween(cities: List<String>): List<List<String>> {
    if (cities.size == 1) return listOf(cities)
    return cities.flatMap { city -> allRoutesBetween(cities - city).map { listOf(city) + it } }
}

private fun distanceOf(route: List<String>): Int = route.zip(route.drop(1)).sumBy { (start, end) ->
    distances.getValue(setOf(start, end))
}
