package zakhi.aoc2017

import zakhi.helpers.floorSqrt
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.pairs
import kotlin.math.absoluteValue


fun main() {
    val particles = matchEachLineOf("aoc2017/day20", Regex("""p=<(-?\d+),(-?\d+),(-?\d+)>, v=<(-?\d+),(-?\d+),(-?\d+)>, a=<(-?\d+),(-?\d+),(-?\d+)>""")) {
        (p0, p1, p2, v0, v1, v2, a0, a1, a2) -> Particle(
            ParticleData(p0.toLong(), v0.toLong(), a0.toLong()),
            ParticleData(p1.toLong(), v1.toLong(), a1.toLong()),
            ParticleData(p2.toLong(), v2.toLong(), a2.toLong())
        )
    }.toList()

    val closestParticle = particles.minByOrNull { it.distanceAt(1_000_000) }
    println("The closest particle will be ${particles.indexOf(closestParticle)}")

    val collisionTimes = calculateCollisionTimes(particles).toSortedMap()
    val particlesLeft = particles.toMutableSet()

    collisionTimes.forEach { (_, collidingParticles) ->
        particlesLeft.removeAll(collidingParticles)
    }

    println("The number of remaining particles is ${particlesLeft.size}")
}


private fun calculateCollisionTimes(particles: List<Particle>): Map<Long, Set<Particle>> =
    particles.pairs().mapNotNull { (a, b) -> a.timeCollidesWith(b)?.let { time -> time to setOf(a, b) } }
        .groupingBy { (time, _) -> time }
        .aggregate { _, allParticles, (_, particles), _ -> particles + allParticles.orEmpty() }


private data class Particle(
    val x: ParticleData,
    val y: ParticleData,
    val z: ParticleData
) {
    fun distanceAt(time: Long): Long = axes.sumOf { (position, velocity, acceleration) ->
        (position + time * velocity + time * (time + 1) * acceleration / 2).absoluteValue
    }

    fun timeCollidesWith(other: Particle): Long? {
        val timesPerAxis = axes.zip(other.axes).map { (mine, others) ->
            val (dp, dv, da) = mine - others
            quadraticEquationWholeSolutions(da, 2 * dv + da, 2 * dp).filter { it > 0 }.toSet()
        }

        return timesPerAxis.reduce { a, b -> a.intersect(b) }.minOrNull()
    }

    private val axes: List<ParticleData> get() = listOf(x, y, z)
}

private data class ParticleData(
    val position: Long,
    val velocity: Long,
    val acceleration: Long
) {
    operator fun minus(other: ParticleData): ParticleData =
        ParticleData(position - other.position, velocity - other.velocity, acceleration - other.acceleration)
}


private fun quadraticEquationWholeSolutions(a: Long, b: Long, c: Long): Set<Long> {
    if (a == 0L) return if (b == 0L) emptySet() else notNullSetOf((-c).wholeDiv(b))

    val disc = (b * b - 4 * a * c)
    if (disc < 0 || !disc.isSquare()) return emptySet()

    return notNullSetOf((-b + disc.floorSqrt()).wholeDiv(2 * a), (-b - disc.floorSqrt()) .wholeDiv(2 * a))
}

private fun Long.wholeDiv(other: Long): Long? = (this / other).takeIf { it * other == this }

private fun Long.isSquare(): Boolean = floorSqrt().let { it * it } == this

private fun <T: Any> notNullSetOf(vararg elements: T?): Set<T> = elements.filterNotNull().toSet()
