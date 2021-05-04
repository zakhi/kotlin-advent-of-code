package zakhi.aoc2015

import zakhi.collections.flip
import zakhi.collections.pairs
import zakhi.input.matchEntireTextOf


fun main() {
    val winningEquipment = equipmentSets().sortedBy { equipment -> equipment.sumOf { it.cost } }.first { equipment ->
        val player = createPlayer(equipment)
        val boss = createBoss()

        winnerOfFightBetween(player, boss) == player
    }

    println("The least amount of money spent to win is ${winningEquipment.sumOf { it.cost }}")

    val losingEquipment = equipmentSets().sortedByDescending { equipment -> equipment.sumOf { it.cost } }.first { equipment ->
        val player = createPlayer(equipment)
        val boss = createBoss()

        winnerOfFightBetween(player, boss) == boss
    }

    println("The most amount of money spent and lose is ${losingEquipment.sumOf { it.cost }}")
}


private val weapons = listOf(
    Equipment(cost = 8, damage = 4),
    Equipment(cost = 10, damage = 5),
    Equipment(cost = 25, damage = 6),
    Equipment(cost = 40, damage = 7),
    Equipment(cost = 74, damage = 8)
)

private val armors = listOf(
    Equipment(cost = 13, armor = 1),
    Equipment(cost = 31, armor = 2),
    Equipment(cost = 53, armor = 3),
    Equipment(cost = 75, armor = 4),
    Equipment(cost = 102, armor = 5)
)

private val rings = listOf(
    Equipment(cost = 25, damage = 1),
    Equipment(cost = 50, damage = 2),
    Equipment(cost = 100, damage = 3),
    Equipment(cost = 20, armor = 1),
    Equipment(cost = 40, armor = 2),
    Equipment(cost = 80, armor = 3)
)

private fun equipmentSets(): Sequence<List<Equipment>> = sequence {
    weapons.forEach { weapon ->
        yield(listOf(weapon))

        armors.forEach { armor ->
            yield(listOf(weapon, armor))

            rings.forEach { ring ->
                yield(listOf(weapon, ring))
                yield(listOf(weapon, armor, ring))
            }

            rings.pairs().forEach { (firstRing, secondRing) ->
                yield(listOf(weapon, firstRing, secondRing))
                yield(listOf(weapon, armor, firstRing, secondRing))
            }
        }
    }
}

private fun createBoss() = matchEntireTextOf("aoc2015/day21", Regex("""Hit Points: (\d+)\nDamage: (\d+)\nArmor: (\d+)""")) { (hitPoints, damage, armor) ->
    Fighter(hitPoints.toInt(), damage.toInt(), armor.toInt())
}

private fun createPlayer(equipments: List<Equipment>) = Fighter(
    initialHitPoints = 100,
    damage = equipments.sumOf { it.damage },
    armor = equipments.sumOf { it.armor }
)

private fun winnerOfFightBetween(first: Fighter, second: Fighter): Fighter {
    var fighters = first to second

    while (true) {
        fighters.first.hit(fighters.second)
        if (fighters.second.isDead) return fighters.first

        fighters = fighters.flip()
    }
}

private data class Equipment(
    val cost: Int,
    val damage: Int = 0,
    val armor: Int = 0
)

class Fighter(
    initialHitPoints: Int,
    private val damage: Int,
    private val armor: Int
) {

    val isDead: Boolean get() = hitPoints <= 0

    fun hit(other: Fighter) {
        other.hitPoints -= maxOf(1, damage - other.armor)
    }

    private var hitPoints = initialHitPoints
}
