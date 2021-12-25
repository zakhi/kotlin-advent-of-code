package zakhi.aoc2018

import zakhi.helpers.combinations
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.maxBy
import kotlin.math.absoluteValue


fun main() {
    val nanobots = matchEachLineOf("aoc2018/day23", Regex("""pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(-?\d+)""")) { (x, y, z, r) ->
        Nanobot(x.toLong(), y.toLong(), z.toLong(), r.toLong())
    }

    val strongestBot = nanobots.maxBy { it.radius }
    val botsInRange = nanobots.count { it.inRangeOf(strongestBot) }

    println("The number of bots in the radius of the strongest nanobot is $botsInRange")

    val nanobotGrouper = NanobotGrouper(nanobots)
    val largestGroup = nanobotGrouper.findLargestGroup()

    val shortestDistanceToLargestGroup = largestGroup.maxOf { it.distanceFromOrigin }
    println("The shortest distance to the largest group is $shortestDistanceToLargestGroup")
}


private class NanobotGrouper(private val bots: List<Nanobot>) {

    private val neighbors = buildMap<Nanobot, MutableSet<Nanobot>> {
        bots.combinations(2).filter { (a, b) -> a.intersectsWith(b) }.forEach { (a, b) ->
            getOrPut(a) { mutableSetOf() }.add(b)
            getOrPut(b) { mutableSetOf() }.add(a)
        }
    }

    private val groups = mutableSetOf<Set<Nanobot>>()

    fun findLargestGroup(): Set<Nanobot> {
        findGroups(emptySet(), bots.toSet(), emptySet())
        return groups.maxBy { it.size }
    }

    private fun findGroups(current: Set<Nanobot>, potential: Set<Nanobot>, excluded: Set<Nanobot>) {
        var p = potential
        var x = excluded

        if (p.isEmpty() && x.isEmpty()) {
            groups.add(current)
        } else {
            val u = (p + x).maxBy { v -> n(v).size }
            (p - n(u)).forEach { v  ->
                findGroups(current + v, p intersect n(v), x intersect n(v))
                p  = p - v
                x = x + v
            }
        }
    }

    private fun n(bot: Nanobot) = neighbors.getValue(bot)
}

private data class Nanobot(
    val x: Long,
    val y: Long,
    val z: Long,
    val radius: Long
) {
    fun inRangeOf(other: Nanobot): Boolean =
        distanceFrom(other) <= other.radius

    fun intersectsWith(other: Nanobot): Boolean =
        distanceFrom(other) <= radius + other.radius

    val distanceFromOrigin: Long get() {
        if (x <= radius) return 0
        return listOf(x ,y, z).sumOf { it.absoluteValue } - radius
    }

    private fun distanceFrom(other: Nanobot) =
        listOf(x - other.x, y - other.y, z - other.z).sumOf { it.absoluteValue }
}
