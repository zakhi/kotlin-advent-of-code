package zakhi.aoc2022

import zakhi.helpers.linesOf
import zakhi.helpers.tryMatch


fun main() {
    val commands = linesOf("aoc2022/day7").toList()

    val root = Directory()
    followCommands(commands, root)

    val directorySizes = calculateAllDirectorySizes(root)
    val totalSmallSizes = directorySizes.filter { it < 100000 }.sum()
    println("The total size of small directories is $totalSmallSizes")

    val unusedSpace = 70000000 - root.size
    val requiredSpace = 30000000 - unusedSpace

    val directoryToDeleteSize = directorySizes.filter { it >= requiredSpace }.min()
    println("The size of the smallest directory to delete is $directoryToDeleteSize")
}


private fun followCommands(commands: List<String>, root: Directory) {
    var current = root

    commands.forEach { command ->
        tryMatch<Unit>(command) {
            Regex("""\$ cd /""") to { current = root }
            Regex("""\$ cd \.\.""") to { current = current.parent ?: root }
            Regex("""\$ cd (\w+)""") to { (name) -> current = current.subDirectory(name) }
            Regex("""dir (\w+)""") to { (name) -> current.addSubDirectory(name) }
            Regex("""(\d+) ([\w.]+)""") to { (size, name) -> current.addFile(name, size.toInt()) }
        }
    }
}

private fun calculateAllDirectorySizes(root: Directory): List<Int> = buildList {
    fun collectSizes(directory: Directory) {
        add(directory.size)
        directory.subDirectories.values.forEach { collectSizes(it) }
    }

    collectSizes(root)
}

private class Directory(val parent: Directory? = null) {
    val files = mutableMapOf<String, Int>()
    val subDirectories = mutableMapOf<String, Directory>()

    fun addSubDirectory(name: String) {
        subDirectories[name] = Directory(this)
    }

    fun addFile(name: String, size: Int) {
        files[name] = size
    }

    fun subDirectory(name: String): Directory = subDirectories.getValue(name)

    val size: Int get() = files.values.sum() + subDirectories.values.sumOf { it.size }
}
