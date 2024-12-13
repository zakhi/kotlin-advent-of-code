package zakhi.aoc2022

import zakhi.helpers.linesOf


fun main() {
    val elements = linesOf("aoc2022/day20").map { it.toLong() }.toList()
    val encryptedFile = EncryptedFile().apply { addAll(elements) }

    encryptedFile.mix()
    println("The sum of the groove coordinates is ${encryptedFile.grooveCoordinatesSum}")

    val fileWithKey = EncryptedFile(811589153).apply { addAll(elements) }

    repeat(10) { fileWithKey.mix() }
    println("The sum of the groove coordinates with the key is ${fileWithKey.grooveCoordinatesSum}")
}


private class MixNode(val value: Long) {
    var next: MixNode = this
        private set

    var previous: MixNode = this
        private set

    fun insertAfter(node: MixNode) {
        remove()

        val nextNode = node.next
        node.next = this
        this.next = nextNode
        this.previous = node
        nextNode.previous = this
    }

    fun insertBefore(node: MixNode) {
        remove()

        val previousNode = node.previous
        node.previous = this
        this.previous = previousNode
        this.next = node
        previousNode.next = this
    }

    private fun remove() {
        previous.next = next
        next.previous = previous
    }
}

private class EncryptedFile(
    private val decryptionKey: Int = 1
) {
    private val nodes = mutableListOf<MixNode>()

    val grooveCoordinatesSum: Long get() = sequenceOf(1000, 2000, 3000).map { valueAt(it) }.sum()

    fun addAll(values: List<Long>) {
        values.forEach { add(it) }
    }

    fun add(value: Long) {
        val node = MixNode(value)
        if (nodes.isNotEmpty()) node.insertAfter(nodes.last())

        nodes.add(node)
    }

    fun mix() {
        nodes.forEach { node ->
            val forwardSteps = (node.value * decryptionKey).mod(nodes.size - 1)

            if (forwardSteps > nodes.size / 2) {
                moveBackwards(node, nodes.size - 1 - forwardSteps)
            } else {
                moveForwards(node, forwardSteps)
            }
        }
    }

    private fun moveForwards(node: MixNode, steps: Int) {
        repeat(steps) { node.insertAfter(node.next) }
    }

    private fun moveBackwards(node: MixNode, steps: Int) {
        repeat(steps) { node.insertBefore(node.previous) }
    }

    private fun valueAt(index: Int): Long {
        var current = nodes.first { it.value == 0L }
        repeat(index.mod(nodes.size)) { current = current.next }

        return current.value * decryptionKey
    }
}
