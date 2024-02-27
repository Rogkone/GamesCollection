package minesweeper

data class Cell(
    val x: Int,
    val y: Int,
    var isRevealed: Boolean = false,
    var isMine: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMinesCount: Int = 0

)
