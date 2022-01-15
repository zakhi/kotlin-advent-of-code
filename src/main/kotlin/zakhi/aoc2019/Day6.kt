package zakhi.aoc2019

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.minimalDistance


fun main() {
    val orbits = matchEachLineOf("aoc2019/day6", Regex("""(\w+)\)(\w+)""")) { (orbited, orbiting) -> orbiting to orbited }.toMap()
    val orbitCounts = mutableMapOf<String, Int>()

    fun countOrbits(`object`: String): Int = orbitCounts.getOrPut(`object`) {
        orbits[`object`]?.let { 1 + countOrbits(it) } ?: 0
    }

    orbits.keys.forEach { countOrbits(it) }
    val totalOrbits = orbitCounts.values.sum()

    println("The number of total orbits is $totalOrbits")

    val transfers = minimalDistance(orbits.getValue("YOU"), { it == orbits.getValue("SAN")}) { `object` ->
        val orbiting = orbits.filterValues { it == `object` }.keys.toMutableSet()
        orbits[`object`]?.let { orbiting.add(it) }

        orbiting.map { it to 1 }
    }

    println("The number of transfers required is $transfers")
}
