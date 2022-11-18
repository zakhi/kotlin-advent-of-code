package zakhi.aoc2015

import zakhi.helpers.combinations
import zakhi.helpers.fail
import zakhi.helpers.linesOf
import zakhi.helpers.product


fun main() {
    val packages = linesOf("aoc2015/day24").map { it.toLong() }.toList()

    val firstGroupOfThree = smallestFirstGroupOf(packages, numberOfGroups = 3) ?: fail("Could not find configuration of 3 groups")
    println("The quantum entanglement of the first of 3 groups is ${firstGroupOfThree.product()}")

    val firstGroupOfFour = smallestFirstGroupOf(packages, numberOfGroups = 4) ?: fail("Could not find configuration of 4 groups")
    println("The quantum entanglement of the first of 4 groups is ${firstGroupOfFour.product()}")
}


fun smallestFirstGroupOf(packages: List<Long>, numberOfGroups: Int): List<Long>? {
    if (numberOfGroups == 1) return packages
    if (packages.sum() % numberOfGroups > 0) return null

    val requiredSum = packages.sum() / numberOfGroups

    return (1..packages.size).asSequence().firstNotNullOfOrNull { groupSize ->
        val candidates = packages.combinations(groupSize).filter { it.sum() == requiredSum }.sortedBy { it.product() }
        candidates.filter { group -> smallestFirstGroupOf(packages - group, numberOfGroups - 1) != null }.firstOrNull()
    }
}
