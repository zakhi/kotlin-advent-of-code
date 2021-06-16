package zakhi.aoc2016

import zakhi.aoc2016.ComponentType.GENERATOR
import zakhi.aoc2016.ComponentType.MICROCHIP
import zakhi.helpers.combinations
import zakhi.helpers.product
import zakhi.helpers.linesOf
import zakhi.helpers.minHeapOf
import zakhi.helpers.mapDestructured


fun main() {
    val floors = linesOf("aoc2016/day11").map { line ->
        val generators = Regex("""(\w+) generator""").findAll(line).mapDestructured { (name) -> Component(name, GENERATOR) }
        val microchips = Regex("""(\w+)-compatible microchip""").findAll(line).mapDestructured { (name) -> Component(name, MICROCHIP) }
        Floor(generators + microchips)
    }.toList()

    val minimalSteps = minimalStepsToTarget(FloorState(floors))
    println("Minimal steps required is $minimalSteps")

    val extraComponents = listOf("elerium", "dilithium").flatMap {
        listOf(Component(it, GENERATOR), Component(it, MICROCHIP))
    }

    val actualFloors = floors.mapIndexed { index, floor ->
        when (index) {
            0 -> floor + extraComponents
            else -> floor
        }
    }

    val actualMinimalSteps = minimalStepsToTarget(FloorState(actualFloors))
    println("Actual minimal steps required is $actualMinimalSteps")
}


private fun minimalStepsToTarget(state: FloorState): Int {
    val visited = mutableSetOf<FloorState>()
    val heap = minHeapOf(state to 0)

    while (true) {
        val (currentState, distance) = heap.remove()
        if (currentState.isFinal) return distance

        visited.add(currentState)

        currentState.nextPossibleStates().filterNot { it in visited }.forEach {
            heap[it] = minOf(heap[it], distance + 1)
        }
    }
}

private data class FloorState(
    private val floors: List<Floor>,
    private val elevatorFloor: Int = 0
) {
    val isFinal: Boolean get() = floors.dropLast(1).all { it.isEmpty }
    val isValid: Boolean get() = floors.all { it.isValid }

    fun nextPossibleStates(): List<FloorState> {
        val nextStates = nextElevatorFloors().product(nextComponentsToMove()).map { (newElevatorFloor, componentsToMove) ->
            val newFloors = floors.mapIndexed { index, floor ->
                when (index) {
                    elevatorFloor -> floors[elevatorFloor] - componentsToMove
                    newElevatorFloor -> floors[newElevatorFloor] + componentsToMove
                    else -> floor
                }
            }

            FloorState(newFloors, newElevatorFloor)
        }

        return nextStates.filter { it.isValid }.distinct().toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FloorState) return false

        return identifier == other.identifier
    }

    override fun hashCode(): Int = identifier.hashCode()

    private fun nextElevatorFloors() = listOf(-1, 1).map { elevatorFloor + it }.filter { it in floors.indices }

    private fun nextComponentsToMove(): List<List<Component>> {
        val availableComponents = floors[elevatorFloor].components.toList()
        return (availableComponents.combinations(1) + availableComponents.combinations(2)).toList()
    }

    private val identifier: String by lazy {
        val componentGroups = floors.flatMapIndexed { index, floor ->
            floor.components.map { it to index }
        }.groupBy { (component, _) -> component.element }

        val componentFloors = componentGroups.map { (_, components) ->
            val (generatorFloor, microchipFloor) = components.sortedBy { it.first.type }.map { it.second }
            generatorFloor to microchipFloor
        }.sortedBy { it.toString() }

        "$elevatorFloor:${componentFloors.joinToString(",")}"
    }
}

data class Floor(
    val components: List<Component>
) {
    private val microchips get() = components.filter { it.type == MICROCHIP }.map { it.element }
    private val generators get() = components.filter { it.type == GENERATOR }.map { it.element }

    val isEmpty get() = components.isEmpty()

    val isValid: Boolean get() {
        if (microchips.isEmpty() || generators.isEmpty()) return true
        return microchips.all { it in generators }
    }

    operator fun plus(components: List<Component>) = Floor(this.components + components)
    operator fun minus(components: List<Component>) = Floor(this.components - components)
}

enum class ComponentType {
    GENERATOR, MICROCHIP
}

data class Component(val element: String, val type: ComponentType)
