package zakhi.aoc2017

import zakhi.helpers.matchEachLineOf


fun main() {
    val components = matchEachLineOf("aoc2017/day24", Regex("""(\d+)/(\d+)""")) { (first, second) ->
        Component(first.toInt(), second.toInt())
    }.toSet()

    val bridges = findBridges(components)

    val strength = bridges.maxOf { it.sum() }
    println("The strongest bridge has strength of $strength")

    val maxLength = bridges.maxOf { it.size }
    val strengthOfLongest = bridges.filter { it.size == maxLength }.maxOf { it.sum() }
    println("The longest bridge has strength of $strengthOfLongest")
}


private fun findBridges(components: Set<Component>, bridge: Bridge = emptyList()): List<Bridge> {
    val nextComponents = components.filter { it.canAttachTo(bridge) }
    if (nextComponents.isEmpty()) return listOf(bridge)

    return nextComponents.flatMap { component ->
        findBridges(components - component, component.attachTo(bridge))
    }
}

private typealias Bridge = List<Int>

private data class Component(
    val first: Int,
    val second: Int
) {
    fun canAttachTo(bridge: Bridge): Boolean = bridge.lastPort in listOf(first, second)

    fun attachTo(bridge: Bridge): Bridge =
        bridge + if (bridge.lastPort == first) listOf(first, second) else listOf(second, first)

    private val Bridge.lastPort: Int get() = lastOrNull() ?: 0
}
