package zakhi.aoc2018

import zakhi.aoc2018.CartDirection.*
import zakhi.aoc2018.DirectionChange.*
import zakhi.helpers.*


fun main() {
    val input = linesOf("aoc2018/day13").toList().flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, char ->
            if (char == ' ') null else (x to y) to char
        }
    }.toMap()

    val track = createTrack(input)

    val firstCollision = findFirstCollision(createCarts(input), track)
    println("The first collision occurs at $firstCollision")

    val lastCart = findLastRemainingCart(createCarts(input), track)
    println("The last remaining cart is at ${lastCart.position}")
}


private fun createTrack(input: Map<Point, Char>) = input.mapValues { (_, char) ->
    when (CartDirection.of(char)) {
        Left, Right -> '-'
        Up, Down -> '|'
        else -> char
    }
}

private fun createCarts(input: Map<Pair<Int, Int>, Char>) =
    input.filterValues { it in CartDirection.symbols }.map { (position, char) ->
        Cart(position, CartDirection.of(char)!!)
    }

private fun findFirstCollision(carts: List<Cart>, track: Map<Point, Char>): Point {
    while (true) {
        for (cart in carts.inOrderOfPosition()) {
            cart.move(track)
            if (carts.any { it.collidesWith(cart) }) return cart.position
        }
    }
}

private fun findLastRemainingCart(carts: List<Cart>, track: Map<Point, Char>): Cart {
    val remainingCarts = carts.toMutableList()

    while (remainingCarts.size > 1) {
        for (cart in remainingCarts.inOrderOfPosition()) {
            if (cart in remainingCarts) {
                cart.move(track)

                remainingCarts.find { it.collidesWith(cart) }?.let { collidedCart ->
                    remainingCarts.removeAll(listOf(cart, collidedCart))
                }
            }
        }
    }

    return remainingCarts.first()
}

private fun List<Cart>.inOrderOfPosition(): List<Cart> =
    sortedWith(compareBy<Cart> { it.position.y }.thenBy { it.position.x })

private class Cart(
    initialPosition: Point,
    private var direction: CartDirection
) {
    private var nextIntersection = TurnLeft

    var position = initialPosition
        private set

    fun move(track: Map<Point, Char>) {
        changeDirectionIfNeeded(track)
        position += direction.offset
    }

    fun collidesWith(other: Cart): Boolean = this != other && position == other.position

    private fun changeDirectionIfNeeded(track: Map<Point, Char>) {
        direction = with(direction) {
            when (track.getValue(position)) {
                '\\' -> changeDirection(if (direction in listOf(Up, Down)) TurnLeft else TurnRight)
                '/' -> changeDirection(if (direction in listOf(Up, Down)) TurnRight else TurnLeft)
                '+' -> changeDirection(nextIntersection).also { nextIntersection = nextIntersection.next() }
                else -> direction
            }
        }
    }
}

private enum class CartDirection(
    private val symbol: Char,
    val offset: Point
) {
    Up('^', 0 to -1),
    Right('>', 1 to 0),
    Down('v', 0 to 1),
    Left('<', -1 to 0);

    companion object {
        fun of(char: Char) = values().find { it.symbol == char }
        val symbols = values().map { it.symbol }
    }

    fun changeDirection(change: DirectionChange) = when (change) {
        TurnLeft -> values().toList().cyclicPreviousFrom(ordinal)
        TurnRight -> values().toList().cyclicNextFrom(ordinal)
        GoStraight -> this
    }
}

private enum class DirectionChange {
    TurnLeft, GoStraight, TurnRight;

    fun next() = values().toList().cyclicNextFrom(ordinal)
}
