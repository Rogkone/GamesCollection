package myCompose

import CardGame.*
import CardGame.DrawCardResponse
import androidx.compose.desktop.ui.tooling.preview.Preview
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

import cardCount
import cardHeight
import deckId
import deckSize
import numberOfHumans
import playerCount
import stateMainWindow
import trickString

object cardGameCompose {

    @Composable
    fun printPlayerHands(game: CardGame) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(minOf(playerCount / 2, 3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(game.players.size) { index ->
                Row(
                    modifier = Modifier.height(250.dp).width(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    printSingleHand(game.players[index])
                }
            }
        }
    }

    @Composable
    fun printPlayedCards(playedCards: MutableMap<String, Card>) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (card in playedCards) {
                Column {
                    Text(card.key)
                    Image(
                        painterResource("Cards/${card.value.code}.png"),
                        contentDescription = "",
                        modifier = androidx.compose.ui.Modifier.height(cardHeight)
                    )
                }
            }
        }
    }

    @Composable
    fun printSingleHand(player: CardPlayer) {
        Column {
            Text("${player.name}'s CardGame.Hand:")
            Row(
                //modifier = Modifier.padding(bottom = 16.dp)
            ) {
                player.hand.cards.forEachIndexed { index, card ->
                    val active = index == player.selectedCardIndex
                    if (player.isAI)
                        Image(
                            painterResource("Cards/back.png"),
                            contentDescription = "",
                            modifier = androidx.compose.ui.Modifier.height(cardHeight)
                        )
                    else {
                        Image(
                            painterResource("Cards/${card.code}.png"),
                            contentDescription = "",
                            modifier = Modifier
                                .height(height = if (active) cardHeight.times(1.2.toFloat()) else cardHeight)
                                .clickable { player.selectedCardIndex = index }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun printTrumpCard(game: CardGame) {
        Column {
            Text("Trump")
            Image(
                painterResource("Cards/${game.trumpCard.code}.png"),
                contentDescription = "",
                modifier = androidx.compose.ui.Modifier.height(cardHeight)
            )
        }
    }

    @Composable
    fun printScoreboard(game: CardGame) {
        var totalScore = 0
        Row {
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(game.trumpCard.suit)
                for (player in game.players) {
                    Text("${player.name} - ${player.score}")
                    totalScore += player.score
                }
            }
        }
    }

    @Composable
    fun cardGameMain(gameState: MutableState<CardGame>) {
        var showErrorDialog by remember { mutableStateOf(false) }
        var winnerText = ""
        var playedCards by mutableStateOf(gameState.value.gameRound.playedCardsHash)
        var nextButtonText by remember { mutableStateOf(if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) "Next" else "play selected card") }
        var currentPlayerName by remember { mutableStateOf(gameState.value.players[gameState.value.currentPlayerIndex].name) }
        if (gameState.value.playedRounds == cardCount) {
            winnerText = (gameState.value.getWinner(gameState.value.players))
        }
        if (showErrorDialog) {
            errorDialog(
                message = "Please choose a card",
                onDismiss = {
                    showErrorDialog = false
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                printPlayedCards(playedCards)
                if (gameState.value.playedRounds != 0) {
                    gameState.value.gameRound.playedCardsHash.clear()
                } else
                    Text("")
            }
        }
        playerHands(gameState.value)
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!gameState.value.isGameOver())
                    Text("$currentPlayerName has to play!")
                Spacer(modifier = androidx.compose.ui.Modifier.height((stateMainWindow().size.height / 8) * 5))
                Text(winnerText)
                Spacer(modifier = Modifier.height(32.dp))
                Text(trickString)
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                printTrumpCard(gameState.value)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                printScoreboard(gameState.value)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {

                                if (!gameState.value.isGameOver()) {

                                    Button(onClick = {
                                        if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
                                            getNextAction(gameState)
                                            gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
                                        } else {
                                            if (gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex == -1 && gameState.value.gameRound.playedCards.size != gameState.value.players.size) {
                                                showErrorDialog = true //if no card chosen
                                            } else {
                                                getNextAction(gameState)
                                                gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
                                            }
                                        }
                                        if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
                                            nextButtonText = "Next"
                                        } else {
                                            nextButtonText = "play selected card!"
                                        }

                                    }) {
                                        Text(nextButtonText)
                                    }
                                } else {
                                    Button(onClick = {
                                        getNextAction(gameState)
                                    }) {
                                        Text(if (gameState.value.playedRounds == cardCount) "New Game" else "Next")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun errorDialog(message: String, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "Error")
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = "OK")
                }
            },
            properties = DialogProperties()
        )
    }



    fun getNextAction(gameState: MutableState<CardGame>) {
        if (gameState.value.playedRounds == cardCount) {
            DrawCardResponse.shuffelDeck(deckId)
            trickString = ""
            gameState.value = CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize)
        } else {
            if (gameState.value.gameRound.playedCards.size == gameState.value.players.size)
                trickString = gameState.value.endRound()
            else
                gameState.value.currentPlayerTurn(gameState.value.players[gameState.value.currentPlayerIndex])
        }
    }


    @Composable
    @Preview
    fun playerHands(game: CardGame) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                printPlayerHands(game)
            }
        }
    }

}