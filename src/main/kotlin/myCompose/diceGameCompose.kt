package myCompose

import DiceGame.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object diceGameCompose {

    val diceHeight = 100.dp

    @Composable
    fun diceGameMain(gameViewModel: DiceGameViewModel, onBack: () -> Unit) {

        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f)) {
                table(gameViewModel, Modifier.weight(1f))
                dice(gameViewModel, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onBack, modifier = Modifier.padding(16.dp).height(75.dp).width(200.dp).clip(CircleShape)) {
                    Text("Back to Main", fontSize = 25.sp)
                }
            }
        }
    }

    @Composable
    fun table(gameViewModel: DiceGameViewModel, modifier: Modifier = Modifier) {
        val gameState = gameViewModel.gameState.collectAsState().value
        val roll = gameState.roll
        val pointSheet = gameState.pointSheet
        val fatList: List<String> = listOf("Upper Sum", "Bonus", "Upper Total", "Lower Total", "Total", "Upper Sum")
        val column1Weight = .1f
        val column2Weight = .1f
        val column3Weight = .2f

        LazyColumn(modifier = modifier.fillMaxHeight().padding(16.dp)) {
            // header
            item {
                Row() {
                    tableCell(text = "YAHTZEE", weight = column1Weight)
                    tableCell(text = "Points", weight = column2Weight)
                    tableCell(text = "How to calculate score", weight = column3Weight)
                }
            }
            // content
            itemsIndexed(pointSheet.scores.entries.toList()) { _, entry ->
                Row(Modifier.fillMaxWidth()) {
                    tableCell(
                        text = entry.key,
                        weight = column1Weight,
                        borderWeight = if (entry.key in fatList) 2.dp else 1.dp
                    )
                    if (entry.key in fatList) {
                        tableCell(
                            text = entry.value?.toString() ?: roll.calcCurrentScore(entry.key).toString(),
                            weight = column2Weight,
                            borderWeight = 2.dp
                        )
                    } else {
                        tableCellClickable(
                            text = entry.value?.toString() ?: roll.calcCurrentScore(entry.key).toString(),
                            weight = column2Weight,
                            gameViewModel = gameViewModel,
                            key = entry.key
                        )
                    }
                    var howToText = ""
                    when (entry.key) {
                        "One" -> howToText = "Add all 1s."
                        "Two" -> howToText = "Add all 2s."
                        "Three" -> howToText = "Add all 3s."
                        "Four" -> howToText = "Add all 4s."
                        "Five" -> howToText = "Add all 5s."
                        "Six" -> howToText = "Add all 6s."
                        "Bonus" -> howToText = "Add 35 if upper total >= 63"
                        "1 Pair" -> howToText = "Add total of all dice if 2 are the same"
                        "2 Pair" -> howToText = "Add total of all dice if 2 sets of 2 are the same"
                        "3 of a kind" -> howToText = "Add total of all dice if 3 are the same."
                        "4 of a kind" -> howToText = "Add total of all dice if 4 are the same."
                        "Full House" -> howToText = "Score 25 points for a full house."
                        "Sm. Street" -> howToText = "Score 30 points for a small straight (4 sequential dice)."
                        "Lg. Street" -> howToText = "Score 40 points for a large straight (5 sequential dice)."
                        "Chance" -> howToText = "Add total of all dice."
                        "Yahtzee" -> howToText = "Score 50 points for a Yahtzee (5 of a kind)."
                    }
                    tableCell(
                        text = howToText,
                        weight = column3Weight,
                        borderWeight = if (entry.key in fatList) 2.dp else 1.dp
                    )
                }
            }
        }
    }

    @Composable
    fun RowScope.tableCell(
        text: String,
        weight: Float,
        borderWeight: Dp = 1.dp,
        background: Color = Color.White
    ) {
        Text(
            text = text,
            Modifier
                .background(background)
                .border(borderWeight, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(25.dp)
        )
    }

    @Composable
    fun RowScope.tableCellClickable(
        text: String,
        weight: Float,
        gameViewModel: DiceGameViewModel,
        key: String
    ) {
        val round = gameViewModel.gameState.value
        Row(
            Modifier
                .background(if (round.pointSheet.scores[key] != null) Color.Green else Color.White, RectangleShape)
                .clickable {
                    if (round.allowedToWrite && round.pointSheet.scores[key] == null) {
                        gameViewModel.writeScore(key, gameViewModel.gameState.value.roll.calcCurrentScore(key))
                        round.allowedToWrite = false
                    } else gameViewModel.setRollFirstDialog(true)
                }
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(25.dp)
                .fillMaxSize()
        ) {
            Text(text)
        }
    }

    @Composable
    fun dice(gameViewModel: DiceGameViewModel, modifier: Modifier = Modifier) {
        val gameState by gameViewModel.gameState.collectAsState()
        val diceRoll = gameState.roll
        val sortedDice = diceRoll.dice.sortedBy { it.value }

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text("Click on a die to save it for the next roll.", fontSize = 20.sp)
                Text("Click on a field in the points column to write down your score.", fontSize = 20.sp)
            }
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

            Text("Roll ${diceRoll.rollCount + 1}/3", fontSize = 20.sp)

            Button(
                onClick = {
                    if (diceRoll.rollCount < 2 && gameState.allowedToWrite) gameViewModel.rollDice()
                    else if (gameState.isGameComplete()) gameViewModel.setShowNameInputDialog(true)
                    else gameViewModel.resetDiceOrPrepareNewGame()
                },
                modifier = Modifier.height(150.dp).width(300.dp).clip(CircleShape),
                shape = CircleShape,
            ) {
                Text(
                    if (diceRoll.rollCount < 2 && gameState.allowedToWrite) "Roll"
                    else if (gameState.isGameComplete()) "Save Data"
                    else "New Roll",
                    fontSize = 50.sp
                )
            }
            Button(
                onClick = {
                    gameViewModel.setShowRulesDialog(true)
                },
                modifier = Modifier.height(150.dp).width(300.dp).clip(CircleShape),
                shape = CircleShape,
            ) {
                Text("Show rules", fontSize = 50.sp)
            }
            Spacer(modifier = Modifier.size(50.dp))
        }
        nameInputDialog(gameViewModel)
        writeFirstDialog(gameViewModel)
        rollFirstDialog(gameViewModel)
        rulesDialog(gameViewModel)
    }


    @Composable
    fun nameInputDialog(gameViewModel: DiceGameViewModel) {
        val showDialog = gameViewModel.showNameInputDialog.collectAsState()
        val userName = gameViewModel.userName.collectAsState()

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    gameViewModel.setShowNameInputDialog(false)
                },
                title = {
                    Text(text = "Enter Your Name:")
                },
                text = {
                    Text("You got ${gameViewModel.gameState.value.pointSheet.calcLowerTotal()+gameViewModel.gameState.value.pointSheet.calcUpperTotal()} points!")
                    TextField(
                        value = userName.value,
                        onValueChange = { newValue ->
                            gameViewModel.setUserName(newValue)
                        },
                        label = { Text("Name") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            gameViewModel.setShowNameInputDialog(false)
                            CBCalls.insertData(gameViewModel)
                            gameViewModel.resetDiceOrPrepareNewGame()
                        }
                    ) {
                        Text("OK")
                    }
                },
            )
        }
    }

    @Composable
    fun writeFirstDialog(gameViewModel: DiceGameViewModel) {
        val showDialog = gameViewModel.showWriteFirstDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "You must write a score before rolling again.") },
                confirmButton = { Button(onClick = { gameViewModel.setWriteFirstDialog(false) }) { Text("OK") } },
            )
        }
    }

    @Composable
    fun rollFirstDialog(gameViewModel: DiceGameViewModel) {
        val showDialog = gameViewModel.showRollFirstDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "You must roll before you can write again.") },
                confirmButton = { Button(onClick = { gameViewModel.setRollFirstDialog(false) }) { Text("OK") } },
            )
        }
    }

    @Composable
    fun rulesDialog(gameViewModel: DiceGameViewModel) {
        val showDialog = gameViewModel.showRulesDialog.collectAsState()
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "    Roll the Dice: At the start of your turn, roll all five dice. Based on the outcome, you can decide to keep some dice and re-roll the others to improve your combination.\n" +
                        "\n" +
                        "    Reroll: You have two chances to re-roll any of the five dice. You don't have to re-roll all the dice, just the ones that you think could improve your score based on the combination you're aiming for. Click on a die to save it for the next roll.\n" +
                        "\n" +
                        "    Choose a Scoring Category: After one to three rolls, choose one of the 15 categories for your scorecard. Each category has its own scoring rules (such as full house, straight, or specific numbers). Your goal is to fill in each category with the highest score possible. If your dice don't match any remaining categories, you'll have to choose one to score zero.") },
                confirmButton = { Button(onClick = { gameViewModel.setShowRulesDialog(false) }) { Text("OK") } },
            )
        }
    }
}

