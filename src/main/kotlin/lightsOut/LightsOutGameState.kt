package lightsOut

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LightsOutGameState(initBoard:LightsOutBoard) {
    var board by mutableStateOf(initBoard)
    var isSolved by mutableStateOf(false)

    private val _showRulesDialog = MutableStateFlow(false)
    val showRulesDialog: StateFlow<Boolean> = _showRulesDialog.asStateFlow()


    fun setShowRulesDialog(show: Boolean) {
        _showRulesDialog.value = show
    }
    fun toggleCells(x:Int, y:Int){
        if(isSolved) return
        val cellsCopy = board.cells.map { it.map { cell -> cell.copy() }.toTypedArray() }.toTypedArray()
        toggleSingleCell(cellsCopy[y][x])

        if (isCellInBounds(x, y+1)) toggleSingleCell(cellsCopy[y+1][x])
        if (isCellInBounds(x, y-1)) toggleSingleCell(cellsCopy[y-1][x])
        if (isCellInBounds(x+1, y)) toggleSingleCell(cellsCopy[y][x+1])
        if (isCellInBounds(x-1, y)) toggleSingleCell(cellsCopy[y][x-1])
        board = board.copy(cells=cellsCopy)
    }
    private fun toggleSingleCell(cell: Cell){
        cell.isLit = !cell.isLit
    }

    private fun isCellInBounds(x:Int, y:Int):Boolean{
        if (x !in 0..<board.size) return false
        if (y !in 0 ..<board.size) return false
        return true
    }

    fun checkIfSolved():Boolean {
        isSolved = board.cells.flatten().count() { cell -> cell.isLit} == 0
        return isSolved
    }
}