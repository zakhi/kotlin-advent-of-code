package zakhi.aoc2018

import zakhi.helpers.linesOf
import zakhi.helpers.maxBy
import zakhi.helpers.tryMatch


fun main() {
    val input = linesOf("aoc2018/day4").sorted().toList()
    val guards = parse(input)

    val longestSleeper = guards.maxBy { it.totalSleepMinutes }
    println("The most sleeping guard and minute multiplication is ${longestSleeper.id * longestSleeper.mostSleepingMinute}")

    val mostConsistentSleeper = guards.maxBy { it.totalTimesSleptAt(it.mostSleepingMinute) }
    println("The most consistent guard and minute multiplication is ${mostConsistentSleeper.id * mostConsistentSleeper.mostSleepingMinute}")
}


private fun parse(input: List<String>): List<Guard> {
    val guards = mutableMapOf<Int, Guard>()
    var currentGuardId = 0
    var sleepStart = 0

    input.forEach { line ->
        tryMatch<Unit>(line) {
            Regex(""".*Guard #(\d+) begins shift""") to { (id) ->
                currentGuardId = guards.computeIfAbsent(id.toInt()) { Guard(it) }.id
            }
            Regex(""".*:(\d+)] falls asleep""") to { (sleepAt) ->
                sleepStart = sleepAt.toInt()
            }
            Regex(""".*:(\d+)] wakes up""") to { (awakeAt) ->
                guards[currentGuardId] = guards.getValue(currentGuardId) + (sleepStart until awakeAt.toInt())
            }
        }
    }

    return guards.values.toList()
}


private data class Guard(
    val id: Int,
    private val sleepPeriods: List<IntRange> = emptyList()
) {
    val totalSleepMinutes = sleepPeriods.sumOf { it.count() }
    val mostSleepingMinute = (0 until 60).maxBy { minute -> totalTimesSleptAt(minute) }

    fun totalTimesSleptAt(minute: Int) = sleepPeriods.count { minute in it }

    operator fun plus(period: IntRange) = copy(sleepPeriods = sleepPeriods + listOf(period))
}
