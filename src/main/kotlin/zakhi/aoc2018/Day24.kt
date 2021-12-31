package zakhi.aoc2018

import zakhi.aoc2018.Army.*
import zakhi.helpers.entireTextOf
import zakhi.helpers.wholeNumbers
import kotlin.text.RegexOption.DOT_MATCHES_ALL


fun main() {
    val input = entireTextOf("aoc2018/day24")
    val battle = Battle(parseGroups(input))
    battle.fight()

    println("The winner has ${battle.winnerUnits} units")

    val winningBattle = wholeNumbers().firstNotNullOf { boost ->
        Battle(parseGroups(input, boost)).apply { fight() }.takeIf { it.winner == ImmuneSystem }
    }

    println("The winner with the boost has ${winningBattle.winnerUnits} units")
}


private fun parseGroups(input: String, immuneSystemBoost: Int? = null): List<Group> {
    val matchResult = Regex("""Immune System:\n(.+)\nInfection:\n(.+)""", DOT_MATCHES_ALL).matchEntire(input) ?: fail("Cannot parse armies")

    return matchResult.groupValues.drop(1).zip(listOf(ImmuneSystem, Infection)).flatMap { (text, type) ->
        text.trim().lines().map { description -> parseGroup(type, description, immuneSystemBoost?.takeIf { type == ImmuneSystem }) }
    }
}

private fun parseGroup(type: Army, description: String, boost: Int?): Group {
    val values = Regex("""(\d+) units each with (\d+) hit points (\(.+\) )?with an attack that does ([\w\s]+) damage at initiative (\d+)""").matchEntire(description)?.groupValues ?: fail("Invalid groups: $description")
    return Group(
        type,
        units = values[1].toInt(),
        hitPoints = values[2].toInt(),
        attack = parseAttack(values[4], boost ?: 0),
        initiative = values[5].toInt(),
        weaknesses = parseModifiers(values[3], type = "weak"),
        immunities = parseModifiers(values[3], type = "immune")
    )
}

private fun parseAttack(attack: String, boost: Int): Attack {
    val (amount, type) = Regex("""(\d+) (\w+)""").matchEntire(attack)?.destructured ?: fail("invalid attack: $attack")
    return Attack(type, amount.toInt() + boost)
}

private fun parseModifiers(modifiers: String, type: String): List<String> =
    Regex("""$type to ([\w, ]+)""").find(modifiers)?.groupValues?.get(1)?.split(", ") ?: emptyList()


private class Battle(
    private val groups: List<Group>
) {

    val winner: Army? get() = aliveGroups.map { it.type }.distinct().takeIf { it.size == 1 }?.first()
    val winnerUnits get() = winner?.totalUnits ?: fail("No winner detected")

    fun fight() {
        while (true) {
            if (winner != null) return

            val attacks = selectTargets()
            if (attacks.isEmpty()) return

            attacks.entries.sortedByDescending { it.key.initiative }.forEach { (attacker, defender) ->
                if (attacker.units > 0) defender.takeDamageBy(attacker)
            }
        }
    }

    private val aliveGroups get() = groups.filter { it.units > 0 }

    private fun selectTargets(): Map<Group, Group> = buildMap {
        aliveGroups.sortedWith(selectionOrder).forEach { attacker ->
            val validTargets = aliveGroups.filter { it.type != attacker.type && it !in values }

            validTargets.sortedWith(vulnerabilityTo(attacker)).firstOrNull()?.let { target ->
                if (target.damageTakenBy(attacker) > 0) put(attacker, target)
            }
        }
    }

    private fun vulnerabilityTo(attacker: Group) = compareByDescending<Group> { it.damageTakenBy(attacker) }.thenComparing(selectionOrder)

    private val Army.totalUnits: Int get() = groups.filter { it.type == this }.sumOf { it.units }

    private val selectionOrder = compareByDescending<Group> { it.effectivePower }.thenByDescending { it.initiative }
}


private data class Group(
    val type: Army,
    var units: Int,
    val hitPoints: Int,
    val attack: Attack,
    val initiative: Int,
    val weaknesses: List<String>,
    val immunities: List<String>
) {
    val effectivePower get() = units * attack.damage

    fun damageTakenBy(attacker: Group): Int {
        val factor = when(attacker.attack.type) {
            in immunities -> 0
            in weaknesses -> 2
            else -> 1
        }

        return attacker.effectivePower * factor
    }

    fun takeDamageBy(attacker: Group) {
        units = maxOf(0, (units - damageTakenBy(attacker) / hitPoints))
    }
}

private data class Attack(
    val type: String,
    val damage: Int
)

private enum class Army {
    ImmuneSystem, Infection
}

fun fail(message: String? = null): Nothing = throw if (message == null) Exception() else Exception(message)
