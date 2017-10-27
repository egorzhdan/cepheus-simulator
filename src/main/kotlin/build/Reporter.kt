package build

import maze.Move
import maze.Position

/**
 * @author Egor Zhdan
 */
interface Reporter {
    suspend fun moved(move: Move, currentPosition: Position)

    suspend fun badMoveAttempted(move: Move, fromPosition: Position)

    suspend fun debugOutputReceived(output: String)

    suspend fun errorPrinted(output: String)

    suspend fun finished()
}