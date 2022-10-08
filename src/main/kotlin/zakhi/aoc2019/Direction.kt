package zakhi.aoc2019

import zakhi.helpers.Point
import zakhi.helpers.cyclicNextFrom
import zakhi.helpers.cyclicPreviousFrom


enum class Direction(val offset: Point) {
    Up(0 to -1),
    Right(1 to 0),
    Down(0 to 1),
    Left(-1 to 0);

    fun turnRight(): Direction = values().toList().cyclicNextFrom(ordinal)
    fun turnLeft(): Direction = values().toList().cyclicPreviousFrom(ordinal)
}
