package zakhi.aoc2022

import zakhi.helpers.entireTextOf


fun main() {
    val buffer = entireTextOf("aoc2022/day6").trim()

    val packetStartMarker = buffer.findMarker(4)
    println("The number of characters before the packet start marker is $packetStartMarker")

    val messageStartMarker = buffer.findMarker(14)
    println("The number of characters before the message start marker is $messageStartMarker")
}


private fun String.findMarker(markerSize: Int) =
    ((markerSize - 1)..<length).first { slice((it - markerSize + 1)..it).toSet().size == markerSize } + 1
