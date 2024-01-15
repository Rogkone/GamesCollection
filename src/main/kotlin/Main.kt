import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Toolkit

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(240.dp)) // freiraum oben

            val imageCount = 8
            val images = remember { mutableStateListOf(*Array(imageCount) { "back.png" }) }

            Row {
                for (i in 0 until imageCount) {
                    Image(
                        painter = painterResource(images[i]),
                        contentDescription = null,
                        modifier = Modifier
                            .requiredSize(150.dp)
                            .offset (((i*-100)+imageCount*50).dp)
                            .clickable(onClick = {
                                images[i] = "AS.png"
                            })
                    )
                }
            }
        }
    }
}


fun giveHand(cardCount:Int) {

}

fun main() = application {
    val screenSize = Toolkit.getDefaultToolkit().screenSize

    val windowHeight = screenSize.height / 2
    val windowWidth = screenSize.width / 2

    val windowSize = DpSize(windowWidth.dp, windowHeight.dp)

    val state = rememberWindowState(
        size = windowSize,
        position = WindowPosition(
            ((screenSize.width / 2) - (windowWidth / 2)).dp,
            ((screenSize.height / 2) - (windowHeight / 2)).dp
        ) // position in the middle of the screen
    )

    Window(title = "Hello World", onCloseRequest = ::exitApplication, state = state) {
        App()
    }
}
