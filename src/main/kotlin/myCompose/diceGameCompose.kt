package myCompose

import DiceGame.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


object diceGameCompose {

    val diceHeight = 100.dp
    @Composable
    fun diceGameMain(gameState: MutableState<DiceGame>) {
        Row(modifier = Modifier.fillMaxSize()) {
            table(gameState, Modifier.weight(1f))
            dice(gameState.value.gameRound.roll, Modifier.weight(1f))
        }
    }

    @Composable
    fun table(gameState: MutableState<DiceGame>, modifier: Modifier = Modifier) {
        val game = gameState.value
        val gameRound = game.gameRound
        val roll = gameRound.roll
        val pointSheet = game.gameRound.pointSheet

        val column1Weight = .1f
        val column2Weight = .1f
        val column3Weight = .2f

        LazyColumn(modifier = modifier.fillMaxHeight().padding(16.dp)) {
            // header
            item {
                Row() {
                    tableCell(text = "", weight = column1Weight)
                    tableCell(text = "Points", weight = column2Weight)
                    tableCell(text = "How to", weight = column3Weight)
                }
            }
            // content
            items(pointSheet.size) { index ->
                val entry = pointSheet.entries.toList()[index]
                Row(Modifier.fillMaxWidth()) {
                    tableCell(text = entry.key, weight = column1Weight)
                    if (entry.key == "Bonus" || entry.key == "Upper Total" || entry.key == "Lower Total" || entry.key == "Total"){
                        tableCell(
                            text = if (entry.value == null) roll.calcCurrentScore(entry.key).toString()
                            else entry.value.toString(), weight = column2Weight
                        )
                    }
                    else {
                        tableCellButton(
                            text = if (entry.value == null) roll.calcCurrentScore(entry.key).toString()
                            else entry.value.toString(),
                            weight = column2Weight,
                            game = gameState,
                            key = entry.key
                        )
                    }
                    tableCell(text = "", weight = column3Weight)
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
        game:MutableState<DiceGame>,
        key:String
    ) {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(27.dp)
        )
        Button(onClick = { game.value.gameRound.writeScore(key) }){
            Text(text = "Write")
        }
    }

    @Composable
    fun dice(diceRoll: DiceRoll, modifier: Modifier = Modifier) {
        diceRoll.dice.sortBy { it.diceValue }

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                diceRoll.dice.forEach { die ->
                    Image(
                        painterResource("Dice/${die.img}"),
                        contentDescription = "",
                        modifier = Modifier.clickable { die.toggleReRoll() }.height(height = if (!die.reRoll)diceHeight.times(1.2.toFloat()) else diceHeight ),

                    )
                }
            }
            if (diceRoll.rollCount < 2)
            Button(onClick = {
                diceRoll.rollDice()
            }) {
                Text("Roll")
            }
        }

    }
}

