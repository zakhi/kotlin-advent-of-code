package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.wholeNumbers


fun main() {
    val layers = matchEachLineOf("aoc2017/day13", Regex("""(\d+): (\d+)""")) { (depth, range) ->
        FirewallLayer(depth.toInt(), range.toInt())
    }

    val totalSeverity = layers.sumOf { if (it.catchesPacketAt(0)) it.severity else 0 }
    println("The severity of the trip is $totalSeverity")

    val bestTime = wholeNumbers().first { time -> layers.none { it.catchesPacketAt(time) } }
    println("The fewest number of picoseconds required to pass is $bestTime")
}


private class FirewallLayer(
    private val depth: Int,
    private val range: Int,
) {
    val severity = depth * range

    fun catchesPacketAt(time: Int): Boolean = (time + depth).mod((range - 1) * 2) == 0
}
