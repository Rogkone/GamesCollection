package myCompose

import CardGame.*
import DiceGame.DiceGameViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import java.awt.Toolkit


import stateMainWindow

// TODO Farbzwang für Spieler
// TODO AI überarbeiten

object cardGameCompose {
    val cardHeight = (Toolkit.getDefaultToolkit().screenSize.height / 10).dp

    @Composable
    fun cardGameMain(gameViewModel: CardGameViewModel, onBack: () -> Unit) {
        val gameState = gameViewModel.gameState

        if (gameState.value.playedRounds == gameViewModel.cardCount) {
            gameViewModel.setWinnerText(gameState.value.getWinner(gameState.value.players))
        }



        playedCards(gameViewModel)
        playerHands(gameViewModel)
        infoBoard(gameViewModel, onBack)
        selectCardDialog(gameViewModel)
    }

    @Composable
    fun infoBoard(gameViewModel: CardGameViewModel, onBack: () -> Unit) {
        val gameState by gameViewModel.gameState.collectAsState()
        val currentPlayerName by gameViewModel.currentPlayerName.collectAsState()
        val winnerText by gameViewModel.winnerText.collectAsState()
        val nextButtonText by gameViewModel.nextButtonText.collectAsState()

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(if (gameState.gameRound.playedCards.size != gameViewModel.playerCount) "$currentPlayerName has to play!" else "")

                Spacer(modifier = Modifier.height((stateMainWindow().size.height / 8) * 5))
                Text(winnerText)
                Spacer(modifier = Modifier.height(32.dp))
                Text(gameViewModel.trickString)
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                trumpCard(gameViewModel)
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                scoreboard(gameViewModel)
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                Button(
                                    onClick = { gameViewModel.handleNextAction() },
                                    modifier = Modifier.height(150.dp).width(400.dp).clip(CircleShape)
                                ) {
                                    Text(nextButtonText, fontSize = 50.sp)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = onBack,
                                    modifier = Modifier.padding(16.dp).height(75.dp).width(200.dp).clip(CircleShape)
                                ) {
                                    Text("Back to Main", fontSize = 25.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun playerHands(gameViewModel: CardGameViewModel) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(minOf(gameViewModel.playerCount / 2, 3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(gameViewModel.gameState.value.players.size) { index ->
                        Row(
                            modifier = Modifier.height(250.dp).width(100.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            singleHand(gameViewModel.gameState.value.players[index])
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun playedCards(gameViewModel: CardGameViewModel) {
        val playedCards = gameViewModel.gameState.value.gameRound.playedCardsHash
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (card in playedCards) {
                        Column {
                            Text(card.key)
                            Image(
                                painterResource("Cards/${card.value.code}.png"),
                                contentDescription = "",
                                modifier = Modifier.height(cardHeight)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun singleHand(player: CardPlayer) {
        Column {
            Text("${player.name}'s CardGame.Hand:")
            Row {
                player.hand.cards.forEachIndexed { index, card ->
                    val active = index == player.selectedCardIndex
                    if (player.isAI) Image(
                        painterResource("Cards/back.png"),
                        contentDescription = "",
                        modifier = Modifier.height(cardHeight)
                    )
                    else {
                        Image(painterResource("Cards/${card.code}.png"),
                            contentDescription = "",
                            modifier = Modifier.height(height = if (active) cardHeight.times(1.2.toFloat()) else cardHeight)
                                .clickable { player.selectedCardIndex = index })
                    }
                }
            }
        }
    }

    @Composable
    fun trumpCard(gameViewModel: CardGameViewModel) {
        Column {
            Text("Trump")
            Image(
                painterResource("Cards/${gameViewModel.gameState.value.trumpCard.code}.png"),
                contentDescription = "",
                modifier = Modifier.height(cardHeight)
            )
        }
    }

    @Composable
    fun scoreboard(gameViewModel: CardGameViewModel) {
        var totalScore = 0
        Row {
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(gameViewModel.gameState.value.trumpCard.suit)
                for (player in gameViewModel.gameState.value.players) {
                    Text("${player.name} - ${player.score}")
                    totalScore += player.score
                }
            }
        }
    }

    @Composable
    fun selectCardDialog(gameViewModel: CardGameViewModel) {
        val showDialog = gameViewModel.showSelectCardDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "Please select a card!") },
                confirmButton = { Button(onClick = { gameViewModel.setShowSelectCardDialog(false) }) { Text("OK") } },
            )
        }
    }
}
