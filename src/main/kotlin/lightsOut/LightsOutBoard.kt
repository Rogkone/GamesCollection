package lightsOut

import kotlin.random.Random

data class LightsOutBoard (
    val size: Int =5,
    val cells: Array<Array<Cell>>
) {
    companion object {
        fun populateBoard(size: Int): LightsOutBoard {
            val cells = Array(size) { y ->
                Array(size) { x ->
                    Cell(x, y, false)
                }
            }

            val board = LightsOutBoard(size, cells)

            val numberOfToggles = size * 2

            repeat(numberOfToggles) {
                val x = Random.nextInt(size)
                val y = Random.nextInt(size)
                board.toggleCellAndAdjacent(x, y)
            }

            return board
        }
    }

    fun toggleCellAndAdjacent(x: Int, y: Int) {
        cells[y][x].isLit = !cells[y][x].isLit

        if (y > 0) cells[y - 1][x].isLit = !cells[y - 1][x].isLit
        if (y < size - 1) cells[y + 1][x].isLit = !cells[y + 1][x].isLit
        if (x > 0) cells[y][x - 1].isLit = !cells[y][x - 1].isLit
        if (x < size - 1) cells[y][x + 1].isLit = !cells[y][x + 1].isLit
    }
}