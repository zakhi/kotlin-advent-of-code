package zakhi.aoc2016

import zakhi.helpers.matchEachLineOf
import zakhi.helpers.join


fun main() {
    val rooms = matchEachLineOf("aoc2016/day4", Regex("""([a-z-]+)-(\d+)\[(\w+)]""")) { (name, sectorId, checksum) ->
        Room(name, sectorId.toInt(), checksum)
    }

    val realRoomSectorIdSum = rooms.filter { it.isReal }.sumOf { it.sectorId }
    println("The sum of the sector IDs of the real rooms is $realRoomSectorIdSum")

    val northPoleObjectStorageRoom = rooms.first { it.decrypted == "northpole object storage" }
    println("The sector ID of the room where the North Pole objects are stored is ${northPoleObjectStorageRoom.sectorId}")
}


private class Room(
    val name: String,
    val sectorId: Int,
    val checksum: String
) {

    val isReal: Boolean get() =
        letterCounts.keys.sortedWith(compareByDescending<Char> { letterCounts[it] }.thenBy { it }).take(5).join() == checksum

    val decrypted: String get() = name.map { it.rotate(sectorId) }.join()

    private val letterCounts = name.filter { it != '-' }.groupingBy { it }.eachCount()
}

private fun Char.rotate(times: Int): Char = when (this) {
    '-' -> ' '
    else -> 'a' + (this - 'a' + times) % 26
}
