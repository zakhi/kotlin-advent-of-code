package zakhi.aoc2018

import zakhi.aoc2018.FlowDirection.Left
import zakhi.aoc2018.FlowDirection.Right
import zakhi.helpers.*


fun main() {
    val clayTiles = matchEachLineOf("aoc2018/day17", Regex("""([xy])=(\d+), [xy]=(\d+)\.\.(\d+)""")) { (axis1, value1, start2, end2) ->
        (start2.toInt()..end2.toInt()).map { value2 ->
            if (axis1 == "x") value1.toInt() to value2 else value2 to value1.toInt()
        }
    }.flatten()

    val scan = GroundScan(clayTiles)
    scan.simulate()

    println("The water reaches ${scan.waterTiles} tiles")
    println("The retained water amount is ${scan.waterRetained}")
}


private class GroundScan(clayTiles: List<Point>) {

    private val blockedPositions = clayTiles.toMutableSet()
    private val waterPositions = mutableSetOf<Point>()

    private val minY = blockedPositions.minOf { it.y }
    private val maxY = blockedPositions.maxOf { it.y }

    val waterTiles get() = waterPositions.count()
    val waterRetained get() = waterPositions.count { it in blockedPositions }

    fun simulate() {
        while (flowDown(from = 500 to 0) is MoreWaterNeeded) {
            // continue to flow
        }
    }

    private fun flowDown(from: Point): FlowContinuation {
        var current = from

        while (current.y < maxY) {
            val target = current.below
            if (blocked(target)) return flowSideways(from = current)

            if (target.y >= minY) waterPositions += target
            current = target
        }

        return EndReached
    }

    private fun flowSideways(from: Point): FlowContinuation {
        val leftFlow = flowOneSide(from, to = Left)
        val rightFlow = flowOneSide(from, to = Right)

        if (leftFlow is Blocked && rightFlow is Blocked) {
            blockedPositions += leftFlow.blockedPositions + rightFlow.blockedPositions
            return MoreWaterNeeded
        }

        return when {
            leftFlow is MoreWaterNeeded || rightFlow is MoreWaterNeeded -> MoreWaterNeeded
            else -> EndReached
        }
    }

    private fun flowOneSide(from: Point, to: FlowDirection): FlowContinuation {
        var current = from

        while (blocked(current.below)) {
            val target = current + to.offset
            if (blocked(target)) return Blocked((minOf(from.x, current.x)..maxOf(from.x, current.x)).map { it to from.y }.toSet())

            waterPositions += target
            current = target
        }

        return flowDown(current)
    }

    private val Point.below: Point get() = this + (0 to 1)

    private fun blocked(pair: Point): Boolean = pair in blockedPositions
}

private sealed interface FlowContinuation

private object EndReached : FlowContinuation
private object MoreWaterNeeded : FlowContinuation
private class Blocked(val blockedPositions: Set<Point>) : FlowContinuation

private enum class FlowDirection(val offset: Point) {
    Left(-1 to 0),
    Right(1 to 0)
}
