package myCompose

import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import lightsOut.LightsOutBoard
import lightsOut.LightsOutGameState
import minesweeper.Board
import minesweeper.MinesweeperGameState


object MinesweeperCompose {

    @Composable
    fun gameScreen(gameState: MinesweeperGameState, onBack: () -> Unit) {

        board(gameState)
        rulesDialog(gameState)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Spacer(Modifier.weight(0.25f))
                Box(modifier = Modifier.weight(1f)) {
                    Text("Cells revealed: " + gameState.countRevealedCells() + "/" + (gameState.board.size * gameState.board.size - gameState.board.minesCount), fontSize = 40.sp)
                }
                Spacer(Modifier.weight(2.5f))
                Box(modifier = Modifier.weight(1f)) {
                    Text("Flags: " + gameState.countFlaggedCells() + "/" + gameState.board.minesCount, fontSize = 40.sp)

                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(), Arrangement.Center
        ) {
            myButtonList(gameState, onBack)


            if (gameState.checkEverythingRevealed())
                Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
                    Text("You won!", fontSize = 50.sp)
                }
            if (gameState.isExploded)
                Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
                    Text("Boom!", fontSize = 50.sp)
                }
        }

    }

    @Composable
    fun myButtonList(gameState: MinesweeperGameState, onBack:() -> Unit) {
        Column {
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.height(100.dp).width(300.dp).clip(CircleShape),
                    onClick = onBack
                ) {
                    Text("Back to Main", fontSize = 25.sp, textAlign = TextAlign.Center)
                }
                Button(
                    modifier = Modifier.height(100.dp).width(300.dp).clip(CircleShape),
                    onClick = {
                        gameState.board = Board.createBoard(10, 10)
                        gameState.isExploded = false
                    }) {
                    Text(if (gameState.isExploded) "Play again!" else "Start new game", fontSize= 25.sp)
                }
                Button(
                    onClick = {
                        gameState.setShowRulesDialog(true)
                    },
                    modifier = Modifier.height(100.dp).width(300.dp).clip(CircleShape),
                ) {
                    Text("Show rules", fontSize = 25.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }


    @Composable
    fun board(gameState: MinesweeperGameState) {
        Column(Modifier.fillMaxHeight(), Arrangement.Center) {
            gameState.board.cells.forEach { row ->
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    row.forEach { cell ->
                        tableCellClickable(
                            text = if (cell.isRevealed) if (cell.isMine) "M" else if (cell.adjacentMinesCount == 0) "" else cell.adjacentMinesCount.toString() else if (cell.isFlagged) "F" else "",
                            onClick = { gameState.revealCell(cell.x, cell.y) },
                            onRightClick = { gameState.toggleFlagCell(cell.x, cell.y) },
                            background = if (cell.isMine && cell.isFlagged && cell.isRevealed) Color.Green else if (cell.isMine && cell.isRevealed) Color.Red else if (cell.isRevealed) Color.White else  Color.Gray
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun tableCellClickable(text: String, onClick: () -> Unit, onRightClick: () -> Unit, background: Color) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(1.dp, Color.Black)
                .clickable { onClick() }
                .background(background)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> {
                                    if (event.buttons.isPrimaryPressed) {
                                        onClick()
                                    } else if (event.buttons.isSecondaryPressed) {
                                        onRightClick()
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = text, fontSize = 32.sp)
        }
    }

    @Composable
    fun rulesDialog(gameViewModel: MinesweeperGameState) {
        val showDialog = gameViewModel.showRulesDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = {Text("How to Play:\n" +
                        "\n" +
                        "    Uncover Squares: Left-click on a square to uncover it. If you uncover a square with a mine, the game is over. If the square is safe, it will either show a number, indicating how many mines are adjacent to that square, or it will be blank, indicating no adjacent mines.\n" +
                        "\n" +
                        "    Flag Mines: Right-click on a square to place a flag where you suspect a mine is hidden. Flagged squares cannot be uncovered until the flag is removed (right-click again to remove a flag). Use flags to keep track of where you think mines are located.\n" +
                        "\n" +
                        "    Numbers: The number on a square shows the count of mines touching that square (including diagonally). Use these numbers to deduce where mines must be hidden and which adjacent squares are safe to uncover.\n" +
                        "\n" +
                        "Winning the Game:\n" +
                        "\n" +
                        "    To win, uncover all squares that do not contain mines. When all safe squares are revealed, and all mines are correctly flagged, you've successfully cleared the minefield!")},
                confirmButton = { Button(onClick = { gameViewModel.setShowRulesDialog(false) }) { Text("OK") } },
            )
        }
    }

}