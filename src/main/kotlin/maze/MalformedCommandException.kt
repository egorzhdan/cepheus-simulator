package maze

/**
 * @author Egor Zhdan
 */
class MalformedCommandException(val command: String) :
        RuntimeException("Malformed command sent to the robot")