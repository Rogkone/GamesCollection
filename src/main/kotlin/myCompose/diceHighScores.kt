package myCompose

import DiceGame.CBCalls
import DiceGame.DiceGameViewModel
import DiceGame.PointSheet
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import myCompose.diceGameCompose.tableCell
import myCompose.diceGameCompose.tableCellClickable

object diceHighScores {
    @Composable
    fun mainHighScore(onBack: () -> Unit) {
        table(onBack)
    }

    @Composable
    fun table(onBack: () -> Unit) {
        val highScores: List<PointSheet> = CBCalls.getHighScores()
        val scoreKeys = highScores.firstOrNull()?.scores?.keys ?: emptySet()

        val column1Weight = .1f
        val column2Weight = .1f

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
                item {
                    Row() {
                        tableCell(text = "Name", weight = column1Weight)
                        highScores.forEach { tableCell(text = it.name, weight = column2Weight) }
                    }
                    Row() {
                        tableCell(text = "Date", weight = column1Weight)
                        highScores.forEach { tableCell(text = it.date, weight = column2Weight) }
                    }
                }

                scoreKeys.forEach { key ->
                    item {
                        var borderWeight = 1.dp
                        Row(Modifier.fillMaxWidth()) {
                            borderWeight =
                                if (key in listOf("Upper Sum", "Bonus", "Upper Total", "Lower Total", "Total")) 2.dp else 1.dp
                            tableCell(text = key, weight = column1Weight, borderWeight=borderWeight)
                            highScores.forEach { pointSheet ->
                                val scoreValue = pointSheet.scores[key]?.toString() ?: "N/A"
                                tableCell(text = scoreValue, weight = column2Weight, borderWeight=borderWeight)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onBack, modifier = Modifier.padding(16.dp)) {
                    Text("Back to Main")
                }
            }
        }
    }
}