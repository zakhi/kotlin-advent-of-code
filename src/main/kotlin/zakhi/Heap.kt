package zakhi


interface Heap<T : Any> {

    fun remove(): Pair<T, Int>

    operator fun set(element: T, value: Int)

    operator fun get(element: T): Int
}

fun <T: Any> minHeapOf(vararg elements: Pair<T, Int>): Heap<T> = MinHeap<T>().apply {
    elements.forEach { (element, value) -> set(element, value) }
}


class MinHeap<T : Any> : Heap<T> {
    private val indices = mutableMapOf<T, Int>()
    private val array = mutableListOf<Pair<T, Int>>()

    override fun remove(): Pair<T, Int> {
        check(array.isNotEmpty())

        swap(0, array.lastIndex)
        val root = array.removeLast()
        indices.remove(root.first)
        bubbleDown(0)

        return root
    }

    override fun set(element: T, value: Int) {
        if (element in indices) {
            val index = indices.getValue(element)
            val oldValue = valueOf(index)
            array[index] = element to value

            if (value < oldValue) bubbleUp(index) else if (value > oldValue) bubbleDown(index)
        } else {
            array.add(element to value)
            indices[element] = array.lastIndex
            bubbleUp(array.lastIndex)
        }
    }

    override fun get(element: T): Int = indices[element]?.let { valueOf(it) } ?: Int.MAX_VALUE

    private fun bubbleDown(index: Int) {
        children(index).minByOrNull { valueOf(it) }?.let { smallestChildIndex ->
            if (valueOf(index) > valueOf(smallestChildIndex)) {
                swap(index, smallestChildIndex)
                bubbleDown(smallestChildIndex)
            }
        }
    }

    private fun bubbleUp(index: Int) {
        val parentIndex = parent(index) ?: return

        if (valueOf(parentIndex) > valueOf(index)) {
            swap(index, parentIndex)
            bubbleUp(parentIndex)
        }
    }

    private fun swap(first: Int, second: Int) {
        val (firstElement, secondElement) = array[first] to array[second]
        indices[firstElement.first] = second
        array[first] = secondElement

        indices[secondElement.first] = first
        array[second] = firstElement
    }

    private fun parent(index: Int): Int? = if (index > 0) (index - 1) / 2 else null

    private fun children(index: Int): List<Int> = listOf(2 * index + 1, 2 * index + 2).filter { it in array.indices }

    private fun valueOf(index: Int) = array[index].second
}
