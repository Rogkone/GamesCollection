package DiceGame

import com.couchbase.client.kotlin.Cluster
import com.couchbase.client.kotlin.query.execute
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

object CBCalls {
    fun insertData(gameViewModel: DiceGameViewModel) {
        val cluster = Cluster.connect(
            connectionString = "couchbase://127.0.0.1",
            username = "Administrator",
            password = "password",
        )

        val game = gameViewModel.gameState.value
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        try {
            runBlocking {
                val collection = cluster
                    .bucket("dice-game-test")
                    .waitUntilReady(timeout = 10000.milliseconds)
                    .defaultCollection()

                collection.upsert(
                    id = (gameViewModel.userName.value + LocalDateTime.now().format(formatter)),
                    content = mapOf(
                        "date" to LocalDateTime.now().format(formatter),
                        "name" to gameViewModel.userName.value,
                        "one" to game.pointSheet.scores["One"],
                        "two" to game.pointSheet.scores["Two"],
                        "three" to game.pointSheet.scores["Three"],
                        "four" to game.pointSheet.scores["Four"],
                        "five" to game.pointSheet.scores["Five"],
                        "six" to game.pointSheet.scores["Six"],
                        "upperSum" to game.pointSheet.scores["Upper Sum"],
                        "bonus" to game.pointSheet.scores["Bonus"],
                        "upperTotal" to game.pointSheet.scores["Upper Total"],
                        "onePair" to game.pointSheet.scores["1 Pair"],
                        "twoPair" to game.pointSheet.scores["2 Pair"],
                        "threeOfAKind" to game.pointSheet.scores["3 of a kind"],
                        "fourOfAKind" to game.pointSheet.scores["4 of a kind"],
                        "fullHouse" to game.pointSheet.scores["Full House"],
                        "smStreet" to game.pointSheet.scores["Sm. Street"],
                        "lgStreet" to game.pointSheet.scores["Lg. Street"],
                        "chance" to game.pointSheet.scores["Chance"],
                        "yahtzee" to game.pointSheet.scores["Yahtzee"],
                        "lowerTotal" to game.pointSheet.scores["Lower Total"],
                        "total" to game.pointSheet.scores["Total"]
                    )
                )
            }
        } finally {
            runBlocking { cluster.disconnect() }
        }
    }

    fun getHighScores(): List<PointSheet> {
        var highScoresList: MutableList<PointSheet> = mutableListOf()
        val cluster = Cluster.connect(
            connectionString = "couchbase://127.0.0.1",
            username = "Administrator",
            password = "password",
        )

        try {
            runBlocking {
                val queryResult = cluster
                    .query("SELECT a.* FROM default:`dice-game-test` a ORDER BY a.`total` DESC LIMIT 10")
                    .execute()

                queryResult.rows.forEach { row ->
                    val jsonString = String(row.content, Charset.defaultCharset())
                   highScoresList.add(parsePlayerScore(jsonString))
                }
            }
        }
        finally {
            runBlocking { cluster.disconnect() }
        }
        return highScoresList
    }

    private fun parsePlayerScore(jsonString: String): PointSheet {
        val jsonPointSheet = Json.decodeFromString(PointSheetJson.serializer(), jsonString)

        return PointSheet(
            name = jsonPointSheet.name,
            date = jsonPointSheet.date,
            scores = mutableMapOf(
                "One" to jsonPointSheet.one,
                "Two" to jsonPointSheet.two,
                "Three" to jsonPointSheet.three,
                "Four" to jsonPointSheet.four,
                "Five" to jsonPointSheet.five,
                "Six" to jsonPointSheet.six,
                "Upper Sum" to jsonPointSheet.upperSum,
                "Bonus" to jsonPointSheet.bonus,
                "Upper Total" to jsonPointSheet.upperTotal,
                "1 Pair" to jsonPointSheet.onePair,
                "2 Pair" to jsonPointSheet.twoPair,
                "3 of a kind" to jsonPointSheet.threeOfAKind,
                "4 of a kind" to jsonPointSheet.fourOfAKind,
                "Full House" to jsonPointSheet.fullHouse,
                "Sm. Street" to jsonPointSheet.smStreet,
                "Lg. Street" to jsonPointSheet.lgStreet,
                "Chance" to jsonPointSheet.chance,
                "Yahtzee" to jsonPointSheet.yahtzee,
                "Lower Total" to jsonPointSheet.lowerTotal,
                "Total" to jsonPointSheet.total
            )
        )
    }

}