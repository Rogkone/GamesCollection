package minesweeper

data class Board(
    val size: Int,
    val minesCount: Int,
    val cells: Array<Array<Cell>>
) {
    companion object {
        fun createBoard(size: Int, minesCount: Int): Board {
            var cells = Array(size) { y ->
                Array(size) { x -> Cell(x, y, isMine = false, isRevealed = false) }
            }
            cells = placeMines(cells, minesCount)
            cells = populateAdjacentMinesCount(cells)
            return Board(size, minesCount, cells)
        }


        private fun placeMines(cells: Array<Array<Cell>>, minesCount: Int): Array<Array<Cell>> {
            val flatCells = cells.flatten().toMutableList()
            val placedMines = flatCells.shuffled().take(minesCount).onEach { it.isMine = true }

            return cells.map { row ->
                row.map { cell ->
                    if (placedMines.contains(cell)) cell.copy(isMine = true) else cell
                }.toTypedArray()
            }.toTypedArray()
        }


        private fun populateAdjacentMinesCount(cells: Array<Array<Cell>>): Array<Array<Cell>> {
            return cells.mapIndexed { y, row ->
                row.mapIndexed { x, cell ->
                    val count = countAdjacentMines(x, y, cells)
                    cell.copy(adjacentMinesCount = count)
                }.toTypedArray()
            }.toTypedArray()
        }

        private fun countAdjacentMines(x: Int, y: Int, cells: Array<Array<Cell>>): Int {
            val directions = listOf(-1, 0, 1)
            return directions.flatMap { dx ->
                directions.map { dy ->
                    if (!(dx == 0 && dy == 0) && x + dx in cells.indices && y + dy in cells[0].indices && cells[y + dy][x + dx].isMine) 1 else 0
                }
            }.sum()
        }


    }
}
