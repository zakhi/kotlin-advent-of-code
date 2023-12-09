package zakhi.aoc2021

import zakhi.helpers.entireTextOf
import zakhi.helpers.fail
import zakhi.helpers.mapDestructured
import zakhi.helpers.pairs
import kotlin.math.absoluteValue


fun main() {
    val scanners = entireTextOf("aoc2021/day19").split("\n\n").map { section ->
        val title = section.lines().first()
        val index = Regex("""--- scanner (\d+) ---""").matchEntire(title)?.groupValues?.get(1)?.toInt()
            ?: fail("failed to parse $title")
        val beacons = Regex("""(-?\d+),(-?\d+),(-?\d+)""").findAll(section)
            .mapDestructured { (x, y, z) -> Triple(x.toInt(), y.toInt(), z.toInt()) }

        Scanner(index, beacons, index == 0)
    }

    normalize(scanners)

    val totalBeacons = scanners.flatMap { it.beacons }.toSet()
    println("The total number of beacons is ${totalBeacons.size}")

    val maxDistance = scanners.pairs().maxOf { (a, b) -> a.distanceFrom(b) }
    println("The maximum distance between scanners is $maxDistance")
}


private typealias Beacon = Triple<Int, Int, Int>

private fun normalize(scanners: List<Scanner>) {
    val normalizedBy = mutableSetOf<Scanner>()

    while (scanners.any { !it.normalized }) {
        val scanner = scanners.first { it.normalized && it !in normalizedBy }

        scanners.filter { !it.normalized }.forEach { other ->
            other.tryNormalizeBy(scanner)
        }

        normalizedBy.add(scanner)
    }
}

private class Scanner(
    val id: Int,
    beacons: List<Beacon>,
    normalized: Boolean
) {
    override fun toString(): String = "Scanner(id=$id)"

    private val beaconTransformations = if (normalized) listOf(beacons) else transformationMatrices.map { matrix ->
        beacons.map { beacon -> beacon.transform(matrix[0], matrix[1], matrix[2]) }
    }

    val normalized: Boolean get() = this::normalization.isInitialized
    val beacons: List<Beacon> get() = normalization.beacons
    val position: Beacon get() = normalization.position

    private lateinit var normalization: Normalization

    init {
        if (normalized) normalization = Normalization(beacons)
    }

    fun distanceFrom(other: Scanner): Int = normalization.let {
        (position.x - other.position.x).absoluteValue + (position.y - other.position.y).absoluteValue + (position.z - other.position.z).absoluteValue
    }

    fun tryNormalizeBy(source: Scanner) {
        for (transformation in beaconTransformations) {
            for (beacon in transformation) {
                for (testBeacon in source.beacons) {
                    val offset = testBeacon - beacon
                    val translated = transformation.map { it + offset }
                    val common = translated.toSet().intersect(source.beacons.toSet())

                    if (common.size >= 12) {
                        normalization = Normalization(translated, offset)
                        return
                    }
                }
            }
        }
    }
}

private data class Normalization(
    val beacons: List<Beacon>,
    val position: Beacon = Beacon(0, 0, 0)
)

private val Beacon.x get() = first
private val Beacon.y get() = second
private val Beacon.z get() = third

private operator fun Beacon.plus(other: Beacon): Beacon = Triple(x + other.x, y + other.y, z + other.z)
private operator fun Beacon.minus(other: Beacon): Beacon = Triple(x - other.x, y - other.y, z - other.z)

private val transformationMatrices: List<List<Beacon>> = sequence {
    val axes = listOf(
        Beacon(1, 0, 0), Beacon(0, 1, 0), Beacon(0, 0, 1),
        Beacon(-1, 0, 0), Beacon(0, -1, 0), Beacon(0, 0, -1)
    )

    axes.forEachIndexed { i, newX ->
        axes.withIndex().filter { (j, _) -> j % 3 != i % 3 }.forEach { (_, newY) ->
            val newZ = crossProduct(newX, newY)
            yield(listOf(newX, newY, newZ))
        }
    }
}.toList()

private fun crossProduct(first: Beacon, second: Beacon) = Beacon(
    first.y * second.z - first.z * second.y,
    first.z * second.x - first.x * second.z,
    first.x * second.y - first.y * second.x
)

private fun Beacon.transform(newX: Beacon, newY: Beacon, newZ: Beacon) = Beacon(
    x * newX.x + y * newY.x + z * newZ.x,
    x * newX.y + y * newY.y + z * newZ.y,
    x * newX.z + y * newY.z + z * newZ.z
)
