package zakhi.aoc2019

import zakhi.helpers.linesOf


fun main() {
    val formulas = linesOf("aoc2019/day14").map { line ->
        val (source, target) = line.split(" => ")
        Formula(parseChemical(target), source.split(", ").map { parseChemical(it) })
    }.toList()

    val factory = NanoChemicalFactory(formulas)
    val oreForOneFuel = factory.oreNeededForFuel(1)

    println("The amount of ORE to produce 1 FUEL is $oreForOneFuel")

    val fuelForOneTrillionOre = fuelMadeByOre(1_000_000_000_000, factory)
    println("The amount of FUEL created from 1 trillion ORE is $fuelForOneTrillionOre")
}


private fun fuelMadeByOre(quantity: Long, factory: NanoChemicalFactory): Long {
    var minFuel = quantity / factory.oreNeededForFuel(1)
    var maxFuel = minFuel * 10

    while (minFuel < maxFuel) {
        val estimatedFuel = (minFuel + maxFuel) / 2
        if (estimatedFuel == minFuel) break

        val requiredOre = factory.oreNeededForFuel(estimatedFuel)

        if (requiredOre >= quantity) maxFuel = estimatedFuel
        if (requiredOre <= quantity) minFuel = estimatedFuel
    }

    return minFuel
}

private data class Chemical(
    val name: String,
    val quantity: Long
)

private data class Formula(
    val chemical: Chemical,
    val ingredients: List<Chemical>
) {
    fun transform(amount: Long): List<Chemical> {
        val multiplier = amount / chemical.quantity + if (amount % chemical.quantity == 0L) 0 else 1
        return ingredients.map { it.copy(quantity = it.quantity * multiplier) } + chemical.copy(quantity = -1 * multiplier * chemical.quantity)
    }
}

private class NanoChemicalFactory(
    formulas: List<Formula>
) {
    private val formulas = formulas.associateBy { it.chemical.name }
    private val quantities = mutableMapOf<String, Long>().withDefault { 0 }

    fun oreNeededForFuel(fuelQuantity: Long): Long {
        quantities.clear()
        quantities["FUEL"] = fuelQuantity

        while (!onlyOre) {
            val nextChemical = remainingQuantities.entries.first { it.key != "ORE" }.toChemical()
            val formula = formulas.getValue(nextChemical.name)

            formula.transform(nextChemical.quantity).forEach { chemical ->
                quantities[chemical.name] = quantities.getValue(chemical.name) + chemical.quantity
            }
        }

        return quantities.getValue("ORE")
    }

    private val remainingQuantities get() = quantities.filterValues { it > 0 }
    private val onlyOre get() = remainingQuantities.keys == setOf("ORE")

    private fun Map.Entry<String, Long>.toChemical(): Chemical = Chemical(key, value)
}

private fun parseChemical(text: String): Chemical {
    val (quantity, name) = text.split(" ")
    return Chemical(name, quantity.toLong())
}
