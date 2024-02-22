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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.awt.Toolkit

import stateMainWindow

/*  TODO Double click to play card?
    TODO AI auto play ?
    TODO Farbzwang für Spieler
    TODO AI überarbeiten

     */

object cardGameCompose {
    val cardHeight = (Toolkit.getDefaultToolkit().screenSize.height / 10).dp

    @Composable
    fun cardGameMain(gameViewModel: CardGameViewModel, onBack: () -> Unit) {
        val gameState = gameViewModel.gameState.collectAsState()

        playedCards(gameViewModel)
        playerHands(gameViewModel)
        infoBoard(gameViewModel, onBack)
        selectCardDialog(gameViewModel)
        rulesDialog(gameViewModel)

        if (gameState.value.recompTestVar % gameViewModel.playerCount == 0
            && gameState.value.playedRounds != gameState.value.numberOfRounds
        ) {
            gameViewModel.gameState.value.gameRound.playedCards.clear()
            gameViewModel.gameState.value.gameRound.playedCardsHash.clear()
            gameViewModel.trickString = ""
            gameViewModel.setWinnerText("")
        }
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
                Text(
                    if (gameState.gameRound.playedCards.size != gameViewModel.playerCount) "$currentPlayerName has to play!" else "",
                    fontSize = 25.sp
                )

                Spacer(modifier = Modifier.height((stateMainWindow().size.height / 8) * 5))
                Text(winnerText, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(gameViewModel.trickString, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(16.dp))
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
                                    Text(nextButtonText, fontSize = 50.sp, textAlign = TextAlign.Center)
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
                                Button(
                                    onClick = {
                                        gameViewModel.setShowRulesDialog(true)
                                    },
                                    modifier = Modifier.height(75.dp).width(200.dp).clip(CircleShape),
                                    shape = CircleShape,
                                ) {
                                    Text("Show rules", fontSize = 25.sp)
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
                Spacer(Modifier.height(150.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(minOf(gameViewModel.playerCount / 2, 3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(gameViewModel.gameState.value.players.size) { index ->
                        Row(
                            modifier = Modifier.height(250.dp).width(100.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            singleHand(gameViewModel, index)
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
                Spacer(Modifier.height(300.dp))
            }
        }
    }

    @Composable
    fun singleHand(gameViewModel: CardGameViewModel, index: Int) {
        val player = gameViewModel.gameState.value.players[index]
        Column {
            Text("${player.name}'s Hand:", fontSize = 30.sp)
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
            Text("Trump", fontSize = 30.sp)
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
                Text(gameViewModel.gameState.value.trumpCard.suit, fontSize = 30.sp)
                for (player in gameViewModel.gameState.value.players) {
                    Text("${player.name} - ${player.score}", fontSize = 30.sp)
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
    @Composable
    fun rulesDialog(gameViewModel: CardGameViewModel) {
            val showDialog = gameViewModel.showRulesDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "Getting Started:\n" +
                        "\n" +
                        "    Deal: At the beginning of each round, each player is dealt 5 cards from a standard deck. One additional card is drawn and placed face up; the suit of this card is the trump for the round. The rest of the deck is set aside as it won't be used in the round.\n" +
                        "\n" +
                        "    Trump Suit: The suit of the face-up card determines the trump suit, which beats cards of any other suit when played in a trick.\n" +
                        "\n" +
                        "Playing the Game:\n" +
                        "\n" +
                        "    Leading a Trick: A random player leads the first trick by playing any card from their hand. For subsequent tricks, the winner of the last trick leads.\n" +
                        "\n" +
                        "    Following Suit: When a trick is led, all other players must play a card of the same suit if they have one. If a player does not have a card of the leading suit, they may play a trump card or any other card if they have no trumps.\n" +
                        "\n" +
                        "    Winning a Trick: The highest trump card in the trick wins. If no trump card is played, the highest card of the leading suit wins. The winner of a trick collects the cards and leads the next trick.\n" +
                        "\n" +
                        "    No Trumps Played: If in any trick no trumps are played, the highest card of the suit that was led wins the trick.\n" +
                        "\n" +
                        "End of the Round:\n" +
                        "\n" +
                        "    After all 5 tricks have been played, the player who has won the most tricks wins the round.") },
                confirmButton = { Button(onClick = { gameViewModel.setShowRulesDialog(false) }) { Text("OK") } },
            )
        }
    }
}
