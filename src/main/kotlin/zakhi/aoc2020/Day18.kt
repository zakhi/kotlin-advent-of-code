package zakhi.aoc2020

import zakhi.helpers.*


fun main() {
    val expressions = linesOf("aoc2020/day18").toList()

    val results = expressions.map { evaluate(it) }
    println("The sum of the results is ${results.sum()}")

    val resultsWithPrecedence = expressions.map { evaluate(it, withAdditionPrecedence = true) }
    println("The sum of the results with addition precedence is ${resultsWithPrecedence.sum()}")
}


private fun evaluate(expression: String, withAdditionPrecedence: Boolean = false): Long {
    val stack = stackOf<Long>()

    expression.toPostfixNotation(withAdditionPrecedence).forEach { char ->
        if (char.isDigit()) {
            stack.push(char.digitToInt().toLong())
        } else {
            val (first, second) = stack.pop(2)
            stack.push(when (char) {
                '+' -> first + second
                '*' -> first * second
                else -> fail("Invalid operator $char")
            })
        }
    }

    return stack.head() ?: 0L
}

private fun String.toPostfixNotation(withAdditionPrecedence: Boolean = false): List<Char> {
    val output = mutableListOf<Char>()
    val operators = stackOf<Char>()

    forEach { char ->
        if (char.isDigit()) {
            output.add(char)
        } else if (char in listOf('+', '*')) {
            val prevOperator = operators.head()
            if (prevOperator == '+' || (prevOperator == '*' && !withAdditionPrecedence)) output.add(operators.pop())
            operators.push(char)
        } else if (char == '(') {
            operators.push(char)
        } else if (char == ')') {
            while (operators.head() != '(') output.add(operators.pop())
            operators.pop()
        }
    }

    while (operators.isNotEmpty()) output.add(operators.pop())
    return output
}
