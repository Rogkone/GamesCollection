import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
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
import java.awt.Toolkit

val initDeck = CardGame.getNewDeckAsync()
val deckId = initDeck!!.deckID
val deckSize = initDeck!!.remaining
val cardCount = 5
val playerCount = 4
var trickString = ""

@Composable
fun printPlayerHands(game: CardGame) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(game.players.size) { index ->
            printSingleHand(game.players[index])
        }
        if (game.playedRounds != 0) {
            //printPlayedCards(game.gameRound.playedCards)
        }
    }
}

@Composable
fun printPlayedCards(playedCards: MutableList<Card>) { //Not working ?
    Column {
        for (card in playedCards) {
            Text(card.toString())
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
    var winnerText = ""
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(game.trumpCard)
        for (player in game.players) {
            Text("${player.name} - ${player.score}")
            totalScore += player.score
        }
    }
    if (totalScore == cardCount) {
        winnerText = (game.getWinner(game.players))
    }
    Text(winnerText)
}

@Composable
fun myNextButton(gameState: MutableState<CardGame>) {
    playerHands(gameState.value)
    val buttonState by rememberUpdatedState(newValue = gameState.value.playedRounds) // updates UI?
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(450.dp))
            Text(trickString)
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                printScoreboard(gameState.value)
                Button(onClick = {
                    getNextAction(gameState)
                }) {
                    Text(if (gameState.value.playedRounds == cardCount) "New Game" else "Next")
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
    //val buttonState by rememberUpdatedState(newValue = game.playedRounds) // updates UI?
    val players = game.players
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

fun main() = application {
    var game by remember { mutableStateOf(CardGame(playerCount, cardCount, 0, deckId, deckSize)) }
    Window(title = "Games Collection", onCloseRequest = ::exitApplication, state = stateMainWindow()) {

        myNextButton(remember { mutableStateOf(game) })
    }
}