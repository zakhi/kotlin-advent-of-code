package zakhi.aoc2022

import zakhi.aoc2022.Material.*
import zakhi.helpers.matchEachLineOf
import zakhi.helpers.product
import java.util.*
import kotlin.math.ceil


fun main() {
    val blueprints = matchEachLineOf("aoc2022/day19", blueprintRegex) { (id, oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost) ->
        Blueprint(id.toInt(), mapOf(
            Geode to mapOf(
                Ore to geodeRobotOreCost.toInt(),
                Obsidian to geodeRobotObsidianCost.toInt()
            ),
            Obsidian to mapOf(
                Ore to obsidianRobotOreCost.toInt(),
                Clay to obsidianRobotClayCost.toInt()
            ),
            Clay to mapOf(Ore to clayRobotOreCost.toInt()),
            Ore to mapOf(Ore to oreRobotOreCost.toInt())
        ))
    }

    val qualityLevelSum = blueprints.sumOf { it.maxGeodesIn(minutes = 24) * it.id }
    println("The sum of the quality levels is $qualityLevelSum")

    val maxGeodes = blueprints.take(3).map { it.maxGeodesIn(minutes = 32) }.product()
    println("The maximum number of geodes collected multiplied together is $maxGeodes")
}

private val blueprintRegex = Regex("""
        |Blueprint (\d+): Each ore robot costs (\d+) ore.
        |Each clay robot costs (\d+) ore. 
        |Each obsidian robot costs (\d+) ore and (\d+) clay. 
        |Each geode robot costs (\d+) ore and (\d+) obsidian.""".trimMargin().replace("\n", "\\s*"))


private data class Blueprint(
    val id: Int,
    val robotCosts: Map<Material, Map<Material, Int>>,
) {
    fun maxRobotsNeeded(material: Material) = robotCosts.values.mapNotNull { it[material] }.maxOrNull() ?: 0

    fun costToCreate(robotType: Material, material: Material) = robotCosts.getValue(robotType).getOrDefault(material, 0)

    fun maxGeodesIn(minutes: Int): Int {
        val initialState = MaterialCollectionState(this, minutesLeft = minutes)
        val queue = PriorityQueue<MaterialCollectionState>().apply { add(initialState) }
        var maxGeodes = 0

        while (queue.isNotEmpty()) {
            val state = queue.poll()

            if (state.maxGeodesPotential >= maxGeodes) {
                queue.addAll(state.nextStates())
            }

            maxGeodes = maxOf(maxGeodes, state.geodes)
        }

        return maxGeodes
    }
}

private data class MaterialCollectionState(
    val blueprint: Blueprint,
    val robots: Map<Material, Int> = Material.entries.associateWith { 0 } + (Ore to 1),
    val materials: Map<Material, Int> = Material.entries.associateWith { 0 },
    val minutesLeft: Int
) : Comparable<MaterialCollectionState> {

    val geodes get() = materials.getValue(Geode)
    val maxGeodesPotential get() = geodes + minutesLeft * ( numberOfRobotsOf(Geode) + (minutesLeft - 1) / 2)

    fun nextStates() = sequence {
        if (minutesLeft <= 0) return@sequence

        materials.keys.map { robotType ->
            if (needMoreRobotsOf(robotType) && canProduceRobotOf(robotType)) {
                val timeToProduce  = timeToProduceRobotOf(robotType)

                if (timeToProduce <= minutesLeft) {
                    val updatedMaterials = materials.mapValues { (material, amount) -> amount + numberOfRobotsOf(material) * timeToProduce - blueprint.costToCreate(robotType, material) }
                    yield(copy(minutesLeft = minutesLeft - timeToProduce, materials = updatedMaterials, robots = robots + (robotType to numberOfRobotsOf(robotType) + 1)))
                }
            }
        }

        val maxProducedMaterials = materials.mapValues { (material, amount) -> amount + numberOfRobotsOf(material) * minutesLeft }
        yield(copy(minutesLeft = 0, materials = maxProducedMaterials))
    }

    private fun needMoreRobotsOf(material: Material) = material == Geode || numberOfRobotsOf(material) < blueprint.maxRobotsNeeded(material)

    private fun canProduceRobotOf(material: Material): Boolean {
        val materialsNeeded = blueprint.robotCosts.getValue(material).keys
        return materialsNeeded.all { numberOfRobotsOf(it) > 0 }
    }

    private fun timeToProduceRobotOf(robotType: Material): Int {
        val timeNeededForMaterials = materials.maxOf { (material, amount) ->
            val leftToCollect = blueprint.costToCreate(robotType, material) - amount
            if (leftToCollect <= 0) 0 else ceil(leftToCollect.toFloat() / numberOfRobotsOf(material)).toInt()
        }

        return timeNeededForMaterials + 1
    }

    private fun numberOfRobotsOf(it: Material) = robots.getValue(it)

    override fun compareTo(other: MaterialCollectionState): Int = other.geodes - geodes
}

private enum class Material {
    Geode, Obsidian, Clay, Ore
}
