package maze

/**
 * @author Egor Zhdan
 */
data class Cell(
        val wallTop: Boolean = false,
        var wallBottom: Boolean = false,
        var wallLeft: Boolean = false,
        var wallRight: Boolean = false
)