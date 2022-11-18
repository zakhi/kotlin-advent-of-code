package zakhi.aoc2019

import zakhi.aoc2019.Direction.*
import zakhi.helpers.*


fun main() {
    val input = readProgramFrom(entireTextOf("aoc2019/day17"))

    val output = mutableListOf<Long>()
    IntcodeComputer(input, { 0 }, { output.add(it) }).start()
    val scaffolds = Scaffolds.create(output)

    val alignmentParametersSum = scaffolds.intersections.sumOf { it.x * it.y }
    println("The sum of the alignment parameters is $alignmentParametersSum")

    val routeRoutines = RouteRoutines(scaffolds.route())
    val instructionInput = input.toMutableList().apply { set(0, 2) }
    var dust = 0L

    IntcodeComputer(instructionInput, inputProvider = routeRoutines::feed, outputConsumer = { dust = it }).start()
    println("The amount of dust is $dust")
}


private class Scaffolds(
    private val scaffoldPoints: Set<Point>,
    private val robotStartPosition: Point,
    private val robotStartDirection: Direction
) {
    companion object {
        fun create(data: List<Long>): Scaffolds {
            val points = mutableSetOf<Point>()
            lateinit var startPosition: Point
            lateinit var startDirection: Direction

            var (x, y) = 0 to 0

            data.map { it.toInt().toChar() }.forEach { char ->
                when (char) {
                    '#' -> {
                        points.add(x to y)
                        x += 1
                    }
                    '\n' -> {
                        y += 1
                        x = 0
                    }
                    '^', 'v', '>', '<' -> {
                        points.add(x to y)
                        startPosition = x to y
                        startDirection = directionFromChar(char)
                        x += 1
                    }
                    else -> x += 1
                }
            }

            return Scaffolds(points, startPosition, startDirection)
        }
    }

    val intersections: List<Point> get() =
        scaffoldPoints.filter { point -> point.adjacentNeighbors.all { it in scaffoldPoints } }

    fun route(): List<String> = buildList {
        var position = robotStartPosition
        var direction = robotStartDirection
        var steps = 0

        fun addSteps() {
            if (steps > 0) add(steps.toString())
            steps = 0
        }

        while (true) {
            if (position + direction.offset in scaffoldPoints) {
                position += direction.offset
                steps += 1
            } else if (position + direction.turnRight().offset in scaffoldPoints) {
                addSteps()
                add("R")
                direction = direction.turnRight()
            } else if (position + direction.turnLeft().offset in scaffoldPoints) {
                addSteps()
                add("L")
                direction = direction.turnLeft()
            } else {
                addSteps()
                break
            }
        }
    }
}

private class RouteRoutines(route: List<String>) {
    private val feedData = mutableListOf<Char>()

    init {
        val patternedRoute = makeRouteFullyPatterned(PatternedRoute(route.joinToString(","))) ?: fail("Cannot find patterns in $route")

        addInputSequence(patternedRoute.route)
        patternedRoute.patterns.forEach { addInputSequence(it) }
        addInputSequence("n")
    }

    fun feed(): Long = feedData.removeFirst().code.toLong()

    private fun addInputSequence(sequence: String) {
        sequence.charList().forEach { feedData.add(it) }
        feedData.add('\n')
    }
}

private data class PatternedRoute(
    val route: String,
    val patterns: List<String> = emptyList()
) {
    val isFinal: Boolean get() = patterns.size == 3
    val isComplete: Boolean get() = isFinal && route.matches(Regex("""[ABC,]+"""))

    val nextPatterns get() = sequence {
        val startIndex = route.indices.first { route[it] in listOf('R', 'L') }
        val maxEndIndex = minOf(startIndex + 20, route.indices.last())

        for (endIndex in maxEndIndex downTo startIndex) {
            if (route[endIndex] in listOf('A', 'B', 'C', ',')) continue
            if (endIndex < route.indices.last() && route[endIndex + 1] != ',') continue

            val pattern = route.slice(startIndex..endIndex)
            yield(PatternedRoute(route.replace(pattern, nextPatternLetter), patterns + pattern))
        }
    }

    private val nextPatternLetter get() = "ABC"[patterns.size].toString()
}

private fun makeRouteFullyPatterned(patternedRoute: PatternedRoute): PatternedRoute? {
    if (patternedRoute.isComplete) return patternedRoute
    if (patternedRoute.isFinal) return null

    return patternedRoute.nextPatterns.firstNotNullOfOrNull { makeRouteFullyPatterned(it) }
}

private fun directionFromChar(char: Char): Direction = when (char) {
    '^' -> Up
    'v' -> Down
    '>' -> Right
    '<' -> Left
    else -> fail("Invalid direction character $char")
}
