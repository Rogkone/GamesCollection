package minesweeper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MinesweeperGameState(initialBoard: Board) {
    var board by mutableStateOf(initialBoard)
    var isExploded by mutableStateOf(false)
    private val _showRulesDialog = MutableStateFlow(false)
    val showRulesDialog: StateFlow<Boolean> = _showRulesDialog.asStateFlow()

    fun setShowRulesDialog(show: Boolean) {
        _showRulesDialog.value = show
    }
    fun revealCell(x: Int, y: Int) {
        val cellsCopy = board.cells.map { it.map { cell -> cell.copy() }.toTypedArray() }.toTypedArray()

        if (cellsCopy[y][x].isMine && !cellsCopy[y][x].isFlagged && !isExploded) {
            cellsCopy[y][x] = cellsCopy[y][x].copy(isRevealed = true)
            isExploded=true
        }
        fun recursiveReveal(x: Int, y: Int) {
            if (x !in 0 until board.size || y !in 0 until board.size || isExploded) return

            val cell = cellsCopy[y][x]

            if (cell.isRevealed || cell.isFlagged) return

            cellsCopy[y][x] = cell.copy(isRevealed = true)

            if (cell.adjacentMinesCount == 0) {
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        recursiveReveal(x + dx, y + dy)
                    }
                }
            }
        }
        recursiveReveal(x, y)
        board = board.copy(cells = cellsCopy)
    }

    fun toggleFlagCell(x:Int, y:Int){
        val cellsCopy = board.cells.map { it.map { cell -> cell.copy() }.toTypedArray() }.toTypedArray()
        if (isExploded) return
        if (cellsCopy[y][x].isFlagged)
            cellsCopy[y][x] = cellsCopy[y][x].copy(isFlagged = false)
        else if (!cellsCopy[y][x].isRevealed && countFlaggedCells()<board.minesCount)
            cellsCopy[y][x] = cellsCopy[y][x].copy(isFlagged = true)
        board = board.copy(cells = cellsCopy)
    }

    fun checkEverythingRevealed():Boolean {
        return (countRevealedCells() == (board.size*board.size)-board.minesCount )
    }

    fun countRevealedCells(): Int{
        return (board.cells.flatten().count() {cell -> cell.isRevealed})
    }
    fun countFlaggedCells(): Int{
        return (board.cells.flatten().count() {cell -> cell.isFlagged})
    }
}
