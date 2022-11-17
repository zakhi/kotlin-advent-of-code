package zakhi.aoc2020

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2020/day17").flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char -> if (char == '#') HyperCube(listOf(x, y)) else null }
    }.toSet()

    val finalState3d = transformSixCycles(input, dimension = 3)
    println("The number of active 3D cubes after 6 cycles is ${finalState3d.size}")

    val finalState4d = transformSixCycles(input, dimension = 4)
    println("The number of active 4D cubes after 6 cycles is ${finalState4d.size}")
}


private data class HyperCube(
    val coordinates: List<Int>
) {
    operator fun plus(other: HyperCube): HyperCube =
        HyperCube(coordinates.zip(other.coordinates).map { (a, b) -> a + b })

    val neighbors get() = hyperGrid(listOf(-1..1), dimension).map { it + this }.filterNot { it == this }

    fun extendTo(dimension: Int): HyperCube {
        val addedCoordinates = (0 until dimension - this.dimension).map { 0 }
        return HyperCube(coordinates + addedCoordinates)
    }

    private val dimension get() = coordinates.size
}

private fun transformSixCycles(input: Set<HyperCube>, dimension: Int): Set<HyperCube> {
    val initialState = input.map { it.extendTo(dimension) }.toSet()
    return (1..6).fold(initialState) { state, _ -> transform(state, dimension) }
}

private fun transform(state: Set<HyperCube>, dimension: Int): Set<HyperCube> {
    val ranges = (0 until dimension).map { d ->
        state.map { it.coordinates.getOrNull(d) ?: 0 }.range().expand()
    }

    return hyperGrid(ranges, dimension).filter { cube ->
        val count = cube.neighbors.count { it in state }
        if (cube in state) count in 2..3 else count == 3
    }.toSet()
}

private fun hyperGrid(ranges: List<IntRange>, dimension: Int = ranges.size): Sequence<HyperCube> {
    if (dimension == 1) return ranges.first().asSequence().map { value -> HyperCube(listOf(value)) }

    val range = if (dimension <= ranges.size) ranges[dimension - 1] else ranges.first()
    return hyperGrid(ranges, dimension - 1).flatMap { cube ->
        range.map { value -> HyperCube(cube.coordinates + value) }
    }
}

private fun ClosedRange<Int>.expand() = (start - 1)..(endInclusive + 1)
