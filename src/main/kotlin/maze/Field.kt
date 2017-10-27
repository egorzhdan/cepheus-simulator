package maze

import kotlinx.serialization.Serializable

/**
 * @author Egor Zhdan
 */
@Serializable
class Field(val matrix: Array<Array<Cell>>,
            val start: Position) {

    val width: Int
    val height: Int = matrix.size

    init {
        check(matrix.isNotEmpty(), { "field must not be empty" })
        width = matrix.first().size
        matrix.forEach {
            check(it.size == width, { "width must be the same for all rows" })
        }
    }

    fun cell(position: Position): Cell = matrix[position.row][position.col]

    fun isInBounds(position: Position): Boolean =
            position.row in 0 until height && position.col in 0 until width

    fun canMove(move: Move, fromPosition: Position): Boolean {
        val nextPosition = fromPosition.move(move)
        if (!isInBounds(nextPosition)) return false
        if (move != Move.FORWARD) return true

        return when (fromPosition.direction) {
            Direction.TOP -> !cell(fromPosition).wallTop && !cell(nextPosition).wallBottom
            Direction.LEFT -> !cell(fromPosition).wallLeft && !cell(nextPosition).wallRight
            Direction.RIGHT -> !cell(fromPosition).wallRight && !cell(nextPosition).wallLeft
            Direction.BOTTOM -> !cell(fromPosition).wallBottom && !cell(nextPosition).wallTop
        }
    }

    companion object {
        fun parse(string: String): Field {
            return Field(
                    string.removeSuffix("\n").split("\n").map {
                        it.split("_").map {
                            Cell(it.contains("U"), it.contains("D"), it.contains("L"), it.contains("R"))
                        }.toTypedArray()
                    }.toTypedArray(),
                    Position(0, 0, Direction.BOTTOM)
            )
        }
    }

}
