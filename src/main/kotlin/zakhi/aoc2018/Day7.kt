package zakhi.aoc2018

import zakhi.helpers.join
import zakhi.helpers.matchEachLineOf


fun main() {
    val dependencies = matchEachLineOf("aoc2018/day7", Regex("""Step (\w) must be finished before step (\w) can begin.""")) { (required, step) ->
        step to required
    }.groupingBy { it.first }.aggregate { _, dependencies: List<String>?, (_, required), _ ->
        dependencies.orEmpty() + required
    }.withDefault { emptyList() }

    val construction = Construction(dependencies)
    construction.work()

    println("The order of the steps is ${construction.stepOrder}")

    val parallelConstruction = Construction(dependencies, workers = 5)
    parallelConstruction.work()

    println("The time to complete all steps in parallel is ${parallelConstruction.totalTimeWorked}")
}


private class Construction(
    private val dependencies: Map<String, List<String>>,
    workers: Int = 1
) {
    private val stepsDone = mutableListOf<String>()
    private val workers = (1..workers).map { Worker() }

    val stepOrder get() = stepsDone.join()

    var totalTimeWorked = 0
        private set

    fun work() {
        val stepsLeft = (dependencies.values.flatten() + dependencies.keys).toMutableSet()

        while (stepsLeft.isNotEmpty()) {
            val nextStep = stepsLeft.filter { step -> dependencies.getValue(step).all { it in stepsDone } }.minOrNull()
            val availableWorker = findAvailableWorker()

            if (nextStep == null || availableWorker == null) {
                waitForNextAvailableWorker()
            } else {
                availableWorker.startWorkingOn(nextStep)
                stepsLeft.remove(nextStep)
            }
        }

        while (workers.any { it.isWorking }) waitForNextAvailableWorker()
    }

    private fun findAvailableWorker(): Worker? = workers.find { !it.isWorking }

    private fun waitForNextAvailableWorker() {
        workers.filter { it.isWorking }.minOfOrNull { it.timeLeft }?.let { timeToWork ->
            val completedSteps = workers.mapNotNull { it.workFor(timeToWork) }
            stepsDone.addAll(completedSteps.sorted())
            totalTimeWorked += timeToWork
        }
    }
}

private class Worker {
    private var currentStep: String? = null

    var timeLeft = 0
        private set

    val isWorking: Boolean get() = timeLeft > 0

    fun startWorkingOn(step: String) {
        currentStep = step
        timeLeft = step.first().code - 'A'.code + 61
    }

    fun workFor(seconds: Int): String? {
        if (isWorking) {
            timeLeft -= seconds
            if (timeLeft == 0) return currentStep
        }

        return null
    }
}
