package myCompose

import DiceGame.DiceGameViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lightsOut.LightsOutBoard
import lightsOut.LightsOutGameState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign


object LightsOutCompose {
    @Composable
    fun lightsOutGameScreen(gameState: LightsOutGameState, onBack: () -> Unit) {

        board(gameState)
        myButtonList(gameState,onBack)
        rulesDialog(gameState)
    }

    @Composable
    fun myButtonList(gameState: LightsOutGameState, onBack:() -> Unit) {
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
                        gameState.board = LightsOutBoard.populateBoard(gameState.board.size)
                        gameState.isSolved = false
                    }
                ) {
                    Text(if (gameState.isSolved) "Play again!" else "Start new game", fontSize = 25.sp, textAlign = TextAlign.Center)
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
            Spacer(Modifier.height(20.dp))

                Box(modifier = Modifier.fillMaxWidth(), Alignment.Center) {
                    Text(if(gameState.checkIfSolved())"You won!" else "", fontSize = 50.sp)
            }
        }
    }

    @Composable
    fun board(gameState: LightsOutGameState) {
        Column(Modifier.fillMaxHeight(), Arrangement.Center) {
            gameState.board.cells.forEach { row ->
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    row.forEach { cell ->
                        tableCellClickable(
                            onClick = { gameState.toggleCells(cell.x, cell.y) },
                            background = if (cell.isLit) Color.Yellow else Color.Gray
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun tableCellClickable(onClick: () -> Unit, background: Color) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .border(1.dp, Color.Black)
                .clickable { onClick() }
                .background(background),
        )
    }

    @Composable
    fun rulesDialog(gameViewModel: LightsOutGameState) {
        val showDialog = gameViewModel.showRulesDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = {Text("How to Play:\n" +
                        "\n" +
                        "    The Board: You'll start with a grid filled with lights, some of which are turned on. Your task is to turn all of them off.\n" +
                        "\n" +
                        "    Toggling Lights: Tap or click on any light to toggle it. But beware, toggling one light will also toggle its immediate neighbors (up, down, left, right). Diagonal neighbors are not affected.\n" +
                        "\n" +
                        "    Planning Your Moves: It requires strategic thinking and planning to figure out the correct sequence of moves that will leave the board in complete darkness.\n" +
                        "\n" +
                        "Winning the Game:\n" +
                        "\n" +
                        "    You win the game once you've managed to turn off all the lights on the board. Depending on the complexity of the starting pattern, this can be a simple task or a brain-twisting challenge!") },
                confirmButton = { Button(onClick = { gameViewModel.setShowRulesDialog(false) }) { Text("OK") } },
            )
        }
    }

}