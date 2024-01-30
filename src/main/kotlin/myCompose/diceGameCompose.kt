package myCompose

import DiceGame.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

object diceGameCompose {

    @Composable
    fun diceGameMain() {
        val diceRoll = remember { DiceRoll() }

        printDice(diceRoll)
    }

    @Composable
    fun printDice(diceRoll: DiceRoll) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            diceRoll.dice.forEach { die ->
                Column {
                    Image(
                        painterResource("Dice/${die.img}"),
                        contentDescription = "",
                        modifier = androidx.compose.ui.Modifier.height(100.dp)
                    )
                }
            }
            Button(onClick = {
                diceRoll.rollDice(listOf(true, true, true, true, true))
            }) {
                Text("ReRoll")
            }
            Text("${diceRoll.dice.sumOf { it.value }}")
        }
    }

}