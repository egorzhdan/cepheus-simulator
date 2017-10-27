package build

import maze.Field
import maze.MalformedCommandException
import maze.Move

/**
 * @author Egor Zhdan
 */
class Session(
        private val field: Field,
        private val reporter: Reporter
) {
    private val prefix = "/robot "
    private var position = field.start

    suspend fun handleOutput(line: String): String? {
        // If starts with `prefix`, treat as a command
        // Otherwise as a debug output
        if (line.startsWith(prefix)) {
            val cmd = line.removePrefix(prefix)

            val move = when (cmd) {
                "move forward" -> Move.FORWARD
                "turn left" -> Move.TURN_LEFT
                "turn right" -> Move.TURN_RIGHT
                else -> throw MalformedCommandException(cmd)
            }

            if (field.canMove(move, position)) {
                position = position.move(move)
                reporter.moved(move, position)
            } else {
                reporter.badMoveAttempted(move, position)
            }

            // Return sensors info:
            val freeForward = if (field.canMove(Move.FORWARD, position)) "free" else "wall"
            val freeLeft = if (field.canMove(Move.FORWARD, position.move(Move.TURN_LEFT))) "free" else "wall"
            val freeRight = if (field.canMove(Move.FORWARD, position.move(Move.TURN_RIGHT))) "free" else "wall"
            return "$freeLeft $freeForward $freeRight"
        } else {
            reporter.debugOutputReceived(line)
            return null
        }
    }

    suspend fun handleError(line: String) {
        reporter.errorPrinted(line)
    }

    suspend fun finish() {
        reporter.finished()
    }
}