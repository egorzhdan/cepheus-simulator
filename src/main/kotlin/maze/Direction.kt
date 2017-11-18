package maze

/**
 * @author Egor Zhdan
 */
enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT;

    fun rotate(move: Move): Direction = when (this) {
        TOP -> when (move) {
            Move.FORWARD -> TOP
            Move.TURN_LEFT -> LEFT
            Move.TURN_RIGHT -> RIGHT
        }

        LEFT -> when (move) {
            Move.FORWARD -> LEFT
            Move.TURN_LEFT -> BOTTOM
            Move.TURN_RIGHT -> TOP
        }

        BOTTOM -> when (move) {
            Move.FORWARD -> BOTTOM
            Move.TURN_LEFT -> RIGHT
            Move.TURN_RIGHT -> LEFT
        }

        RIGHT -> when (move) {
            Move.FORWARD -> RIGHT
            Move.TURN_LEFT -> TOP
            Move.TURN_RIGHT -> BOTTOM
        }
    }

}