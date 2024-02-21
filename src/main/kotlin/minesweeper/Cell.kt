package minesweeper

data class Cell(
    val x: Int,
    val y: Int,
    val isRevealed: Boolean = false,
    var isMine: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMinesCount: Int = 0

)
