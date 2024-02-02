package myCompose

import DiceGame.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


//TODO
// Hintergrundfarbe für ausgefüllte Felder
// Button formatieren
// Nur einmal schreiben, nur neu Rollen wenn geschrieben
//

object diceGameCompose {

    val diceHeight = 100.dp

    @Composable
    fun diceGameMain(gameViewModel: DiceGameViewModel) {
        val gameState by gameViewModel.gameState.collectAsState()
        Row(modifier = Modifier.fillMaxSize()) {
            table(gameViewModel, Modifier.weight(1f))
            dice(gameViewModel, Modifier.weight(1f))
        }
    }

    @Composable
    fun table(gameViewModel: DiceGameViewModel, modifier: Modifier = Modifier) {
        // Observe gameState as state for recomposition
        val gameState = gameViewModel.gameState.collectAsState().value
        val roll = gameState.roll
        val pointSheet = gameState.pointSheet

        val column1Weight = .1f
        val column2Weight = .1f
        val column3Weight = .2f

        LazyColumn(modifier = modifier.fillMaxHeight().padding(16.dp)) {
            // header
            item {
                Row() {
                    tableCell(text = "YAHTZEE", weight = column1Weight)
                    tableCell(text = "Points", weight = column2Weight)
                    tableCell(text = "How to", weight = column3Weight)
                }
            }
            // content
            itemsIndexed(pointSheet.entries.toList()) { _, entry ->
                Row(Modifier.fillMaxWidth()) {
                    tableCell(text = entry.key, weight = column1Weight)
                    if (entry.key in listOf("Bonus", "Upper Total", "Lower Total", "Total")) {
                        tableCell(
                            text =  entry.value?.toString() ?:roll.calcCurrentScore(entry.key).toString(),
                            weight = column2Weight
                        )
                    } else {
                        tableCellButton(
                            text = entry.value?.toString() ?: roll.calcCurrentScore(entry.key).toString(),
                            weight = column2Weight,
                            gameViewModel = gameViewModel,
                            key = entry.key
                        )
                    }
                    var howToText: String = ""
                    when(entry.key) {
                        "One" -> howToText = "Add all 1s."
                        "Two" -> howToText = "Add all 2s."
                        "Three" -> howToText = "Add all 3s."
                        "Four" -> howToText = "Add all 4s."
                        "Five" -> howToText = "Add all 5s."
                        "Six" -> howToText = "Add all 6s."
                        "Bonus" -> howToText = "Add 35 if upper total >= 63"
                        "1 Pair" -> howToText = "Add total of all dice"
                        "2 Pair" -> howToText = "Add total of all dice"
                        "3 of a kind" -> howToText = "Add total of all dice if 3 are the same."
                        "4 of a kind" -> howToText = "Add total of all dice if 4 are the same."
                        "Full House" -> howToText = "Score 25 points for a full house."
                        "Sm. Street" -> howToText = "Score 30 points for a small straight (4 sequential dice)."
                        "Lg. Street" -> howToText = "Score 40 points for a large straight (5 sequential dice)."
                        "Chance" -> howToText = "Add total of all dice."
                        "Yahtzee" -> howToText = "Score 50 points for a Yahtzee (5 of a kind)."
                    }
                    tableCell(text = howToText, weight = column3Weight)
                }
            }
        }
    }


    @Composable
    fun RowScope.tableCell(
        text: String,
        weight: Float,
    ) {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(27.dp)
        )
    }

    @Composable
    fun RowScope.tableCellButton(
        text: String,
        weight: Float,
        gameViewModel: DiceGameViewModel,
        key: String
    ) {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(27.dp)
        )
        Button(onClick = { gameViewModel.writeScore(key,gameViewModel.gameState.value.roll.calcCurrentScore(key)) }) {
            Text(text = "Write")
        }
    }

    @Composable
    fun dice(gameViewModel: DiceGameViewModel, modifier: Modifier = Modifier) {
        val gameState by gameViewModel.gameState.collectAsState()
        val diceRoll = gameState.roll
        val sortedDice = diceRoll.dice.sortedBy { it.value }

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                sortedDice.forEachIndexed { index, die ->
                    Image(
                        painter = painterResource("Dice/${die.img}"),
                        contentDescription = "",
                        modifier = Modifier
                            .clickable { gameViewModel.toggleDiceReRoll(index) } // Use index here
                            .height(if (!die.reRoll) diceHeight.times(1.2.toFloat()) else diceHeight),
                    )
                }
            }
            Button(onClick = {if (diceRoll.rollCount < 2) gameViewModel.rollDice() else gameViewModel.resetDiceOrPrepareNewGame()   }) {
                Text(if (diceRoll.rollCount < 2) "Roll" else "New Roll")
            }
        }

    }


}

