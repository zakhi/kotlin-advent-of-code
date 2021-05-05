package zakhi.aoc2015

import zakhi.input.linesOf


fun main() {
    val strings = linesOf("aoc2015/day8").toList()

    val totalCharacters = strings.sumOf { it.length }
    val inMemoryCharacters = strings.sumOf { it.memorySize }
    println("Character difference is ${totalCharacters - inMemoryCharacters}")

    val encodedCharacters = strings.sumOf { it.encodedSize }
    println("Encoded character difference is ${encodedCharacters - totalCharacters}")
}


private val String.memorySize: Int get() = Regex("""\\\\|\\"|\\x[0-9a-f]{2}""").replace(this, "*").length - 2
private val String.encodedSize: Int get() = count { it == '\\' || it == '"' } + length + 2
