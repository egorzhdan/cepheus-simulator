package maze

import kotlinx.serialization.Serializable

/**
 * @author Egor Zhdan
 */
@Serializable
enum class Move {
    FORWARD,
    TURN_LEFT,
    TURN_RIGHT
}