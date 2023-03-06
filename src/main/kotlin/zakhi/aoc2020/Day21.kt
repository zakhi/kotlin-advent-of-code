package zakhi.aoc2020

import zakhi.helpers.matchEachLineOf


fun main() {
    val ingredientsAndAllergens = matchEachLineOf("aoc2020/day21", Regex("""([\w ]+) \(contains ([\w, ]+)\)""")) { (ingredientList, allergenList) ->
        ingredientList.split(" ").toSet() to allergenList.split(", ").toSet()
    }

    val allergensAndPossibleIngredients = ingredientsAndAllergens.flatMap { it.second }.distinct().associateWith { allergen ->
        ingredientsAndAllergens.mapNotNull { (ingredients, allergens) -> if (allergen in allergens) ingredients else null }.reduce(Set<String>::intersect).toMutableSet()
    }

    with(allergensAndPossibleIngredients) {
        while (any { it.value.size > 1 }) {
            val resolvedIngredients = values.filter { it.size == 1 }.flatten().toSet()
            values.forEach { possibleIngredients ->
                if (possibleIngredients.size > 1) possibleIngredients.removeAll(resolvedIngredients)
            }
        }
    }

    val ingredientsWithAllergens = allergensAndPossibleIngredients.values.flatten().toSet()
    val safeIngredientAppearances = ingredientsAndAllergens.flatMap { (ingredients, _) -> ingredients - ingredientsWithAllergens }.count()

    println("Ingredients with no allergens appear $safeIngredientAppearances times")

    val dangerousIngredientList = allergensAndPossibleIngredients.entries.sortedBy { it.key }.joinToString(",") { it.value.first() }
    println("The dangerous ingredient list is $dangerousIngredientList")
}
