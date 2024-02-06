package DiceGame

import com.couchbase.client.kotlin.Cluster
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

//QUERY for 5 highest
//SELECT a.* FROM default:`dice-game-test` a ORDER BY a.`total` DESC LIMIT 5

object CBCalls {
    fun insertData(gameViewModel: DiceGameViewModel) {
        val cluster = Cluster.connect(
            connectionString = "couchbase://127.0.0.1",
            username = "Administrator",
            password = "password",
        )

        val game = gameViewModel.gameState.value
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
        try {
            runBlocking {
                val collection = cluster
                    .bucket("dice-game-test")
                    .waitUntilReady(timeout = 10000.milliseconds)
                    .defaultCollection()

                collection.upsert(
                    id = (gameViewModel.userName.value+LocalDateTime.now().format(formatter)),
                    content = mapOf(
                        "date" to LocalDateTime.now().format(formatter),
                        "player_name" to gameViewModel.userName.value,
                        "one" to game.pointSheet.scores["One"],
                        "two" to game.pointSheet.scores["Two"],
                        "three" to game.pointSheet.scores["Three"],
                        "four" to game.pointSheet.scores["Four"],
                        "five" to game.pointSheet.scores["Five"],
                        "six" to game.pointSheet.scores["Six"],
                        "upper_sum" to game.pointSheet.scores["Upper Sum"],
                        "bonus" to game.pointSheet.scores["Bonus"],
                        "upper_total" to game.pointSheet.scores["Upper Total"],
                        "1_pair" to game.pointSheet.scores["1 Pair"],
                        "2_pair" to game.pointSheet.scores["2 Pair"],
                        "3_of_a_kind" to game.pointSheet.scores["3 of a kind"],
                        "4_of_a_kind" to game.pointSheet.scores["4 of a kind"],
                        "full_house" to game.pointSheet.scores["Full House"],
                        "sm_street" to game.pointSheet.scores["Sm. Street"],
                        "lg_street" to game.pointSheet.scores["Lg. Street"],
                        "chance" to game.pointSheet.scores["Chance"],
                        "yahtzee" to game.pointSheet.scores["Yahtzee"],
                        "lower_total" to game.pointSheet.scores["Lower Total"],
                        "total" to game.pointSheet.scores["Total"]
                        ))
            }
        } finally {
            runBlocking { cluster.disconnect() }
        }
    }
}