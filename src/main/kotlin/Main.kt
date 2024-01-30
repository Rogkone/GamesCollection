import CardGame.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.Toolkit
import myCompose.*
import myCompose.cardGameCompose.cardGameMain
import myCompose.diceGameCompose.diceGameMain

// TODO Farbzwang für Spieler
// TODO AI überarbeiten

val initDeck = CardGame.getNewDeckAsync()
val deckId = initDeck!!.deckID
val deckSize = initDeck!!.remaining
const val cardCount = 5
const val playerCount = 4
const val numberOfHumans = 1
var trickString = ""
val cardHeight = (Toolkit.getDefaultToolkit().screenSize.height / 10).dp

fun main() = application {
    Window(title = "Games Collection", onCloseRequest = ::exitApplication, state = stateMainWindow()) {
        var activeGame by remember { mutableStateOf<String?>(null) }

        when (activeGame) {
            "cardGame" -> {
                val cardGame by remember { mutableStateOf(CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize)) }
                cardGameMain(remember { mutableStateOf(cardGame) })
            }
            "diceGame" -> {
                //val diceGame by remember { mutableStateOf(DiceGame()) }
                diceGameMain()
            }
            null -> mainScreen(onGameStart = { game -> activeGame = game })

        }
    }
}


@Composable
fun mainScreen(onGameStart: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(onClick = { onGameStart("cardGame") }) {
            Text("Card Game")
        }
        Button(onClick = { onGameStart("diceGame") }) {
            Text("Dice Game")
        }
    }
}

@Composable
fun stateMainWindow(): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowHeight = screenSize.height / 1.5
    val windowWidth = screenSize.width / 1.5
    val windowSize = DpSize(windowWidth.dp, windowHeight.dp)
    return rememberWindowState(
        size = windowSize,
        position = WindowPosition(
            ((screenSize.width / 2) - (windowWidth / 2)).dp,
            ((screenSize.height / 2) - (windowHeight / 2)).dp
        )
    )
}

