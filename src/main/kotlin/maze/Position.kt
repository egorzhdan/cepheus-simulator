package maze

import kotlinx.serialization.Serializable

/**
 * @author Egor Zhdan
 */
@Serializable
data class Position(val row: Int, val col: Int, var direction: Direction) {
    fun forward() = when (direction) {
        Direction.TOP -> Position(row - 1, col, direction)
        Direction.BOTTOM -> Position(row + 1, col, direction)
        Direction.LEFT -> Position(row, col - 1, direction)
        Direction.RIGHT -> Position(row, col + 1, direction)
    }

    fun move(move: Move) = when (move) {
        Move.TURN_LEFT -> Position(row, col, direction.rotate(move))
        Move.TURN_RIGHT -> Position(row, col, direction.rotate(move))
        Move.FORWARD -> forward()
    }
}
