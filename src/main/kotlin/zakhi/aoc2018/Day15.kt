package zakhi.aoc2018

import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2018/day15").flatMapIndexed { y, row ->
        row.mapIndexed { x, char -> (x to y) to char }
    }.toMap()

    val combat = Combat(input)
    while (!combat.ended) combat.playRound()

    println("The combat outcome is ${combat.outcome}")

    val winningCombat = wholeNumbers(from = 4).firstNotNullOf { elfAttackPower ->
        Combat(input, elfAttackPower).apply { while (!ended && !elfDied) playRound() }.takeUnless { it.elfDied }
    }

    println("The elf-winning combat outcome is ${winningCombat.outcome}")
}


private class Combat(
    map: Map<Point, Char>,
    elfAttackPower: Int = 3
) {
    private var roundsPlayed = 0
    private val field = map.filterValues { it != '#' }.mapValues { '.' }
    private val units = map.mapNotNull { (position, char) ->
        when (char) {
            'G' -> Goblin(position)
            'E' -> Elf(position, elfAttackPower)
            else -> null
        }
    }

    fun playRound() {
        units.sorted().forEach { unit ->
            if (ended) return

            if (unit.alive) {
                unit.move()
                unit.attack()
            }
        }

        roundsPlayed += 1
    }

    val ended: Boolean get() = aliveUnits.none { it is Goblin } || aliveUnits.none { it is Elf }
    val elfDied: Boolean get() = units.any { it is Elf && !it.alive }
    val outcome: Int get() = roundsPlayed * aliveUnits.sumOf { it.hitPoints }

    private val aliveUnits get() = units.filter { it.alive }
    private val CombatUnit.enemyUnits get() = aliveUnits.filter { it::class != this::class }
    private val Point.isOccupied: Boolean get() = aliveUnits.any { it.position == this }

    private fun CombatUnit.move() {
        fun Point.isNextToEnemy() = enemyUnits.any { it.position in adjacentNeighbors }

        if (position.isNextToEnemy()) return
        val target = searchFrom(position) { it.isNextToEnemy() } ?: return

        position = when (target) {
            in position.adjacentNeighbors -> target
            else -> searchFrom(target) { position in it.adjacentNeighbors } ?: return
        }
    }

    private fun CombatUnit.attack() {
        val closeEnemies = enemyUnits.filter { it.position in position.adjacentNeighbors }
        val target = closeEnemies.minWithOrNull(compareBy<CombatUnit> { it.hitPoints }.thenBy(yThenX) { it.position } ) ?: return

        attack(target)
    }

    private fun searchFrom(initialPosition: Point, isTarget: (Point) -> Boolean): Point? {
        var next = listOf(initialPosition)
        val visited = mutableSetOf<Point>()

        while (next.isNotEmpty()) {
            visited.addAll(next)

            next = next.flatMap { it.adjacentNeighbors }.distinct()
                .filter { it in field && it !in visited && !it.isOccupied }
                .sortedWith(yThenX)

            next.find { isTarget(it) }?.let { return it }
        }

        return null
    }
}

private open class CombatUnit(
    var position: Point,
    val attackPower: Int = 3
) : Comparable<CombatUnit> {
    var hitPoints = 200

    val alive get() = hitPoints > 0

    override fun compareTo(other: CombatUnit): Int =
        compareValuesBy(this, other, yThenX) { it.position }

    fun attack(other: CombatUnit) {
        other.hitPoints -= attackPower
    }
}

private class Goblin(position: Point) : CombatUnit(position)
private class Elf(position: Point, attackPower: Int) : CombatUnit(position, attackPower)

private val yThenX: Comparator<Point> get() = compareBy<Point> { it.y }.thenBy { it.x }
