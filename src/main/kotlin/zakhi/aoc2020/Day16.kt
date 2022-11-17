package zakhi.aoc2020

import zakhi.helpers.entireTextOf
import zakhi.helpers.product
import zakhi.helpers.second


fun main() {
    val (rulesText, ticketTest, otherTicketsText) = entireTextOf("aoc2020/day16").trim().split("\n\n")
    val rules = rulesText.lines().map { line ->
        val (name, firstRange, secondRange) = Regex("""(.+): ([\d-]+) or ([\d-]+)""").matchEntire(line)?.destructured ?: throw Exception("Invalid rule $line")
        TicketRule(name, listOf(firstRange, secondRange).map {
            val (start, end) = it.split("-")
            start.toInt() .. end.toInt()
        })
    }

    val ticket = ticketTest.lines().second().split(",").map { it.toInt() }
    val otherTickets = otherTicketsText.lines().drop(1).map { line -> line.split(",").map { it.toInt() } }

    val invalidValues = otherTickets.flatten().filterNot { value -> rules.any { value in it } }.toSet()
    println("The ticket error rate is ${invalidValues.sum()}")

    val validTickets = otherTickets.filterNot { otherTicket -> otherTicket.any { it in invalidValues } }

    val ruleFields = rules.associateWith { rule ->
        ticket.indices.filter { fieldIndex ->
            validTickets.all { it[fieldIndex] in rule }
        }.toMutableSet()
    }

    while (ruleFields.any { it.value.size > 1 }) {
        val (multiFieldRules, singleFieldRules) = ruleFields.entries.partition { it.value.size > 1 }
        val associatedFields = singleFieldRules.flatMap { it.value }.toSet()
        multiFieldRules.forEach { (_, fields) -> fields.removeIf { it in associatedFields } }
    }

    val departureFields = ruleFields.filter { it.key.name.startsWith("departure") }.map { it.value.first() }
    val product = departureFields.map { ticket[it].toLong() }.product()
    println("The product of all departure rules is $product")
}


private data class TicketRule(
    val name: String,
    val ranges: List<IntRange>
) {
    operator fun contains(value: Int) = ranges.any { value in it }
}
