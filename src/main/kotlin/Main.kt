import CardGame.*
import DiceGame.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import java.awt.Toolkit
import myCompose.cardGameCompose.cardGameMain
import myCompose.diceGameCompose.diceGameMain
import myCompose.diceHighScores

fun main() = application {
    Window(title = "Games Collection", onCloseRequest = ::exitApplication, state = stateMainWindow()) {
        var activeGame by remember { mutableStateOf<String?>(null) }

        when (activeGame) {
            "cardGame" -> {
                val cardGameViewModel = remember { CardGameViewModel() }
                cardGameMain(cardGameViewModel) { activeGame = null}
            }
            "DiceGame" -> {
                val diceGameViewModel = remember { DiceGameViewModel() }
                diceGameMain(diceGameViewModel) { activeGame = null }
            }
            "Dice Highscores" -> {
                diceHighScores.mainHighScore { activeGame = null }
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
        Button(
            onClick = { onGameStart("cardGame") },
            modifier = Modifier.height(150.dp).width(400.dp).clip(CircleShape)) {
            Text("Card Game",fontSize = 50.sp)
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = { onGameStart("DiceGame") },
            modifier = Modifier.height(150.dp).width(400.dp).clip(CircleShape)) {
            Text("Dice Game",fontSize = 50.sp)
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = { onGameStart("Dice Highscores") },
            modifier = Modifier.height(150.dp).width(400.dp).clip(CircleShape)) {
            Text("Dice Highscores",fontSize = 50.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun stateMainWindow(): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowHeight = screenSize.height / 1.3
    val windowWidth = screenSize.width / 1.3
    val windowSize = DpSize(windowWidth.dp, windowHeight.dp)
    return rememberWindowState(
        size = windowSize,
        position = WindowPosition(
            ((screenSize.width / 2) - (windowWidth / 2)).dp,
            ((screenSize.height / 2) - (windowHeight / 2)).dp
        )
    )
}

