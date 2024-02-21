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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.sp
import minesweeper.Board
import minesweeper.MinesweeperGameState


object MinesweeperCompose {

    @Composable
    fun gameScreen(gameState: MinesweeperGameState, onBack: () -> Unit) {

        board(gameState)
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
        Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
            Button(onClick = onBack, modifier = Modifier.padding(16.dp)) {
                Text("Back to Main")
            }
        }
        Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
            Button(
                onClick = {
                    gameState.board = Board.createBoard(10, 10)
                    gameState.isExploded = false
                }) {
                Text(if (gameState.isExploded) "Play again!" else "Start new game")
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
                            background = if (cell.isMine && cell.isRevealed) Color.Red else if (cell.isRevealed) Color.White else  Color.Gray
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

}