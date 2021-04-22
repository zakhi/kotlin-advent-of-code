package zakhi.aoc2015

import zakhi.matchEachLineOf


fun main() {
    val highestScore = amountDistributionsOf(ingredients).maxOf { it.totalScore }
    println("The highest-scored cookie has total score of $highestScore")

    val highestScoreWithFixedCalories = amountDistributionsOf(ingredients).filter { it.totalCalories == 500 }.maxOf { it.totalScore }
    println("The highest-scored 500-calorie cookie has total score of $highestScoreWithFixedCalories")
}


private val ingredients = matchEachLineOf("aoc2015/day15", Regex("""\w+: capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)""")) {
    (capacity, durability, flavor, texture, calories) -> Ingredient(capacity.toInt(), durability.toInt(), flavor.toInt(), texture.toInt(), calories.toInt())
}

private fun amountDistributionsOf(ingredientsLeft: List<Ingredient>, amountLeft: Int = 100): Sequence<Map<Ingredient, Int>> = sequence {
    if (ingredientsLeft.size == 1) {
        yield(mapOf(ingredientsLeft.first() to amountLeft))
    } else {
        (0..amountLeft).forEach { amount ->
            val innerCombinations = amountDistributionsOf(ingredientsLeft.drop(1), amountLeft - amount)
            yieldAll(innerCombinations.map { it + (ingredientsLeft.first() to amount) })
        }
    }
}

private val Map<Ingredient, Int>.totalScore: Int get() =
    totalScoreOf { capacity } * totalScoreOf { durability } *
    totalScoreOf { flavor } * totalScoreOf { texture }

private val Map<Ingredient, Int>.totalCalories: Int get() = totalScoreOf { calories }

private fun Map<Ingredient, Int>.totalScoreOf(property: Ingredient.() -> Int): Int =
    maxOf(0, entries.sumBy { (ingredient, amount) -> property(ingredient) * amount })

private data class Ingredient(
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
)
