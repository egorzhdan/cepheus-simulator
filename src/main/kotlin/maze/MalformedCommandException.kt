package maze

/**
 * @author Egor Zhdan
 */
class MalformedCommandException(command: String) :
        RuntimeException("Malformed command sent to the robot: $command")