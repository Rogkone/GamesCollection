import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import coil3.compose.AsyncImage
import java.awt.Toolkit

fun main() = application {
    var game by remember { mutableStateOf(CardGame(playerCount, cardCount, 0, deckId, deckSize)) }
    Window(title = "Games Collection", onCloseRequest = ::exitApplication, state = stateMainWindow()) {
        cardGame(remember { mutableStateOf(game) })
    }
}

val initDeck = CardGame.getNewDeckAsync()
val deckId = initDeck!!.deckID
val deckSize = initDeck!!.remaining
const val cardCount = 5
const val playerCount = 6
var trickString = ""

//val buttonState by rememberUpdatedState(newValue = gameState.value.playedRounds) // updates UI?

@Composable
fun printPlayerHands(game: CardGame) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(game.players.size) { index ->
            Row(
                modifier = Modifier.height(150.dp).width(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                printSingleHand(game.players[index])
            }
        }
    }
}

@Composable
fun printPlayedCards(playedCards: MutableMap<String, Card>) {
    Column {
        for (card in playedCards) {
            Text("${card.key} - ${card.value}")
        }
    }
}

@Composable
fun printSingleHand(player: CardPlayer) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text("${player.name}'s Hand:")
        for (card in player.hand.cards) {
            Text(card.toString())
        }
    }
}

@Composable
fun printScoreboard(game: CardGame) {
    var totalScore = 0
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(game.trumpCard)
        for (player in game.players) {
            Text("${player.name} - ${player.score}")
            totalScore += player.score
        }
    }
}

@Composable
fun cardGame(gameState: MutableState<CardGame>) {
    var winnerText = ""
    if (gameState.value.playedRounds == cardCount) {
        winnerText = (gameState.value.getWinner(gameState.value.players))
    }
    playerHands(gameState.value)
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model ="https://deckofcardsapi.com/static/img/6H.png",
                contentDescription = "6 of Hearts")

            Spacer(modifier = Modifier.height(400.dp))
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
                            if (gameState.value.playedRounds != 0)
                                printPlayedCards(gameState.value.gameRound.playedCardsHash)
                            else
                                Text("")

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

fun getNextAction(gameState: MutableState<CardGame>) {
    if (gameState.value.playedRounds == cardCount) {
        DrawCardResponse.shuffelDeck(deckId)
        trickString = ""
        gameState.value = CardGame(playerCount, cardCount, 0, deckId, deckSize)
    } else {
        trickString = gameState.value.playNextRound()
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

@Composable
fun stateMainWindow(): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowHeight = screenSize.height / 2
    val windowWidth = screenSize.width / 2
    val windowSize = DpSize(windowWidth.dp, windowHeight.dp)
    return rememberWindowState(
        size = windowSize,
        position = WindowPosition(
            ((screenSize.width / 2) - (windowWidth / 2)).dp,
            ((screenSize.height / 2) - (windowHeight / 2)).dp
        )
    )
}

