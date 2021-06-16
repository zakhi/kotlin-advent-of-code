package zakhi.aoc2015

import zakhi.helpers.matchEntireTextOf


fun main() {
    val boss = matchEntireTextOf("aoc2015/day22", Regex("""Hit Points: (\d+)\nDamage: (\d+)""")) { (hitPoints, damage) ->
        Boss(hitPoints.toInt(), damage.toInt())
    }

    val player = Player()

    val minimalSpentMana = minimalWinningGameFrom(GameState(boss, player))
    println("The least amount of mana required to win is $minimalSpentMana")

    val minimalSpentManaForHardDifficulty = minimalWinningGameFrom(GameState(boss, player, hardDifficulty = true))
    println("The least amount of mana required to win in hard difficulty is $minimalSpentManaForHardDifficulty")
}

private fun minimalWinningGameFrom(gameState: GameState, currentMinimum: Int = Int.MAX_VALUE): Int {
    if (gameState.manaSpent >= currentMinimum) return currentMinimum

    val startedGameState = gameState.startTurn()

    if (startedGameState.isOver) {
        return if (startedGameState.playerWon) minOf(currentMinimum, startedGameState.manaSpent) else currentMinimum
    }

    val nextPossibleGameStates = startedGameState.playTurn()

    return nextPossibleGameStates.fold(currentMinimum) { newMinimum, nextGameState ->
        minimalWinningGameFrom(nextGameState, newMinimum)
    }
}


private data class Player(
    private val hitPoints: Int = 50,
    val mana: Int = 500
) {
    val isDead: Boolean get() = hitPoints <= 0

    fun takeDamage(amount: Int) = copy(hitPoints = hitPoints - maxOf(1, amount))

    fun heal(amount: Int) = copy(hitPoints = hitPoints + amount)

    fun spendMana(amount: Int) = copy(mana = mana - amount)

    fun increaseMana(amount: Int) = copy(mana = mana + amount)
}

private data class Boss(
    private val hitPoints: Int,
    val damage: Int
) {
    val isDead: Boolean get() = hitPoints <= 0

    fun takeDamage(amount: Int) = copy(hitPoints = hitPoints - amount)
}

private data class GameState(
    private val boss: Boss,
    private val player: Player,
    val manaSpent: Int = 0,
    private val playerTurn: Boolean = true,
    private val spells: Spells = Spells(),
    private val hardDifficulty: Boolean = false
) {
    val isOver: Boolean get() = player.isDead || boss.isDead
    val playerWon: Boolean get() = boss.isDead

    fun startTurn(): GameState {
        if (isOver) return this

        val damagedPlayer = player.takeDifficultyDamage()
        if (damagedPlayer.isDead) return copy(player = damagedPlayer)

        val (affectedBoss, affectedPlayer) = spells.affect(boss, damagedPlayer)
        val stillActiveSpells = spells.reduceDuration()

        return copy(boss = affectedBoss, player = affectedPlayer, spells = stillActiveSpells)
    }

    fun playTurn() = if (playerTurn) playerTurn() else bossTurn()

    private fun bossTurn(): List<GameState> {
        val damagedPlayer = player.takeDamage(boss.damage - spells.shieldProtection)
        return listOf(copy(player = damagedPlayer, playerTurn = true))
    }

    private fun playerTurn(): List<GameState> {
        val validSpells = spells.notActive.filter { spell -> player.mana >= spell.manaRequired }

        return validSpells.map { spell ->
            val (affectedBoss, affectedPlayer) = spell.cast(boss, player.spendMana(spell.manaRequired))
            val updatedSpells = spells.activate(spell)

            copy(boss = affectedBoss, player = affectedPlayer, manaSpent = manaSpent + spell.manaRequired, playerTurn = false, spells = updatedSpells)
        }
    }

    private fun Player.takeDifficultyDamage() = if (hardDifficulty && playerTurn) takeDamage(1) else this
}

private interface Spell {
    val manaRequired: Int
    val duration: Int get() = 0

    fun cast(boss: Boss, player: Player): Pair<Boss, Player> = boss to player

    fun affect(boss: Boss, player: Player): Pair<Boss, Player> = boss to player
}

private object MagicMissile : Spell {
    override val manaRequired = 53

    override fun cast(boss: Boss, player: Player): Pair<Boss, Player> = boss.takeDamage(4) to player
}

private object Drain : Spell {
    override val manaRequired = 73

    override fun cast(boss: Boss, player: Player): Pair<Boss, Player> = boss.takeDamage(2) to player.heal(2)
}

private object Shield : Spell {
    override val manaRequired = 113
    override val duration = 6
    const val reducedDamage = 7
}

private object Poison : Spell {
    override val manaRequired = 173
    override val duration = 6

    override fun affect(boss: Boss, player: Player): Pair<Boss, Player> = boss.takeDamage(3) to player
}

private object Recharge : Spell {
    override val manaRequired = 229
    override val duration = 5

    override fun affect(boss: Boss, player: Player): Pair<Boss, Player> = boss to player.increaseMana(101)
}

private val allSpells = listOf(
    MagicMissile, Drain, Shield, Poison, Recharge
)

private class Spells(
    private val spellsWithDurations: Map<Spell, Int> = allSpells.associateWith { 0 }
) {
    val notActive: Set<Spell> get() = spellsWithDurations.filterValues { it == 0 }.keys

    val shieldProtection: Int = if (spellsWithDurations.getValue(Shield) > 0) Shield.reducedDamage else 0

    fun affect(boss: Boss, player: Player): Pair<Boss, Player> =
        active.fold(boss to player) { (boss, player), spell -> spell.affect(boss, player) }

    fun reduceDuration(): Spells = Spells(spellsWithDurations.mapValues { maxOf(0, it.value - 1) })

    fun activate(spell: Spell): Spells = Spells(spellsWithDurations + (spell to spell.duration))

    private val active: Set<Spell> get() = spellsWithDurations.filterValues { it > 0 }.keys
}
