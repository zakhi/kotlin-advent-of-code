package zakhi.aoc2021

import zakhi.helpers.matchEachLineOf


fun main() {
    val steps = matchEachLineOf("aoc2021/day22", Regex("""(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""")) { (on, minX, maxX, minY, maxY, minZ, maxZ) ->
        RebootStep(on == "on", Cuboid(minX.toInt()..maxX.toInt(), minY.toInt()..maxY.toInt(), minZ.toInt()..maxZ.toInt()))
    }.toList()

    val smallRangeSteps = steps.filter { it.inSmallRange }

    println("The number of on cubes in the small range is ${countOnCubes(smallRangeSteps)}")
    println("The number of total on cubes is ${countOnCubes(steps)}")
}


private data class Cuboid(
    val xs: IntRange,
    val ys: IntRange,
    val zs: IntRange
) {
    fun count() = xs.count().toLong() * ys.count().toLong() * zs.count().toLong()

    fun intersect(other: Cuboid): Cuboid? {
        val xs = xs.intersect(other.xs)
        val ys = ys.intersect(other.ys)
        val zs = zs.intersect(other.zs)

        return if (xs.isEmpty() || ys.isEmpty() || zs.isEmpty()) null else Cuboid(xs, ys, zs)
    }

    private fun IntRange.intersect(other: IntRange): IntRange {
        val start = maxOf(this.first, other.first)
        val end = minOf(this.last, other.last)
        return start..end
    }
}

private data class RebootStep(
    val on: Boolean,
    val cuboid: Cuboid
) {
    val inSmallRange = listOf(cuboid.xs, cuboid.ys, cuboid.zs).flatMap { listOf(it.first, it.last) }.all { it in -50..50 }
}

private fun countOnCubes(steps: List<RebootStep>): Long {
    val extendedSteps = mutableListOf<RebootStep>()

    steps.forEach { step ->
        extendedSteps.addAll(extendedSteps.mapNotNull { other ->
            other.cuboid.intersect(step.cuboid)?.let { RebootStep(!other.on, it) }
        })

        if (step.on) extendedSteps.add(step)
    }

    return extendedSteps.sumOf {
        val sign = if (it.on) 1L else -1L
        sign * it.cuboid.count()
    }
}
