package zakhi.aoc2015

import zakhi.helpers.matchEachLineOf


fun main() {
    val reindeer = matchEachLineOf("aoc2015/day14", Regex("""\w+ can fly (\d+) km/s for (\d+) seconds, but then must rest for (\d+) seconds.""")) {
        (speed, flyTime, restTime) -> FlyingReindeer(speed.toInt(), flyTime.toInt(), restTime.toInt())
    }

    val raceTime = 2503

    val winningDistance = reindeer.maxOf { it.distanceCoveredIn(raceTime) }
    println("The distance covered by the winner is $winningDistance")

    (1..raceTime).forEach { timePassed -> reindeer.maxByOrNull { it.distanceCoveredIn(timePassed) }?.awardPoint() }

    val winningPoints = reindeer.maxOf { it.points }
    println("The winner has $winningPoints points")
}


private class FlyingReindeer(
    private val speed: Int,
    private val flyTime: Int,
    private val restTime: Int,
) {
    var points: Int = 0
        private set

    fun distanceCoveredIn(seconds: Int): Int {
        val fullRoundsDistance = seconds / (flyTime + restTime) * speed * flyTime
        val remainder = seconds % (flyTime + restTime)
        val remainderDistance = minOf(remainder, flyTime) * speed

        return fullRoundsDistance + remainderDistance
    }

    fun awardPoint() {
        points += 1
    }
}
