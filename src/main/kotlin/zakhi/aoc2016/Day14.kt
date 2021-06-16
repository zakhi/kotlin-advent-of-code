package zakhi.aoc2016

import zakhi.helpers.entireTextOf
import zakhi.helpers.mapDestructured
import zakhi.helpers.md5Hash


fun main() {
    val index = keyIndex(at = 64)
    println("The index of the 64th key is $index")

    val extraHashesIndex = keyIndex(at = 64, extraHashes = true)
    println("The index of the 64th key with extra hashes is $extraHashesIndex")
}


private val input = entireTextOf("aoc2016/day14").trim()

private fun keyIndex(at: Int, extraHashes: Boolean = false): Int =
    KeyGenerator(extraHashes).generateKeys().take(at).last()


private class KeyGenerator(extraHashes: Boolean) {
    private val buffer = ArrayDeque<Pair<Int, String>>()
    private val sequenceCounts = mutableMapOf<String, Int>().withDefault { 0 }
    private val hashTimes = if (extraHashes) 2017 else 1
    private var index  = 0

    init {
        repeat(1000) { addNewHash() }
    }

    fun generateKeys(): Sequence<Int> = sequence {
        while (true) {
            addNewHash()
            val (index, hash) = removeNextHash()
            val repeatingCharacter = Regex("""(\w)\1{2,}""").find(hash)?.groupValues?.get(1) ?: continue

            if (sequenceCounts.getValue(repeatingCharacter) > 0) yield(index)
        }
    }

    private fun addNewHash() {
        val hash = (1..hashTimes).fold("$input$index") { string, _ -> string.md5Hash() }

        buffer.add(index to hash)
        index++

        fiveCharSequences(hash) { (char) ->
            sequenceCounts[char] = sequenceCounts.getValue(char) + 1
        }
    }

    private fun removeNextHash(): Pair<Int, String> {
        val (index, hash) = buffer.removeFirst()

        fiveCharSequences(hash) { (char) ->
            sequenceCounts[char] = maxOf(sequenceCounts.getValue(char) - 1, 0)
        }

        return index to hash
    }

    private fun fiveCharSequences(string: String, block: (MatchResult.Destructured) -> Unit) =
        Regex("""(\w)\1{4,}""").findAll(string).mapDestructured(block)
}
