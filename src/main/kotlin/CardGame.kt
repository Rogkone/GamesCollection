import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlin.random.Random

class CardGame(
    private val numberOfPlayers: Int,
    private val numberOfRounds: Int,
    private val numberOfHumans: Int,
    private val deckId: String,
    private val availableCards: Int,
    private var startingPlayerIndex: Int = 0,
    var trumpCard: String = "HEARTS"
) {
    val players: Array<CardPlayer>
    val rounds: Array<CardGameRound>
    var playedRounds by mutableStateOf(0)
    lateinit var gameRound: CardGameRound
    init {
        val rnd = Random
        if (numberOfPlayers * numberOfRounds > availableCards - 1 || numberOfPlayers * numberOfRounds < 2) {
            throw Exception("Invalid player or rounds count")
        }

        players = Array(numberOfPlayers) { i ->
            val isAI = i >= numberOfHumans
            CardPlayer("Player ${i + 1}", isAI)
        }

        rounds = Array(numberOfRounds) { CardGameRound(deckId) }

        for (i in 0 until numberOfPlayers) {
            players[i].hand.setCards(drawCardFromDeck(numberOfRounds, deckId))
        }
        trumpCard = drawCardFromDeck(1, deckId)[0].suit
        playedRounds=0
        startingPlayerIndex = rnd.nextInt(players.size)
    }

    fun playNextRound(): String {
        if (playedRounds < rounds.size) {
            playedRounds++
            return play()
        }
        return ""
    }


    fun play(): String {
        println("$trumpCard is trump!")
        gameRound = CardGameRound(trumpCard)

        for (i in players.indices) {
            if (gameRound.playedCards.isNotEmpty()) {
                println("Played cards:")
                for (j in gameRound.playedCards.indices) {
                    val playerIndex = (startingPlayerIndex + j) % players.size
                    print("${players[playerIndex].name}: ")
                    gameRound.playedCards[j].printCard()
                }
            }

            val currentPlayerIndex = (i + startingPlayerIndex) % players.size
            val cardIndex = players[currentPlayerIndex].getPlayerCardIndex(gameRound.playedCards, gameRound.trump)
            gameRound.addCard(players[currentPlayerIndex].hand.cards[cardIndex])

            if (i == players.size - 1) {
                val lastPlayerIndex = (startingPlayerIndex + players.size - 1) % players.size
                print("${players[lastPlayerIndex].name}: ")
                gameRound.playedCards[players.size - 1].printCard()
            }

            players[currentPlayerIndex].hand.removeCard(players[currentPlayerIndex].hand.cards[cardIndex])
        }
        val winningPlayerIndex = (gameRound.getWinningPlayer() + startingPlayerIndex) % players.size
        players[winningPlayerIndex].score++
        printScoreBoard()
        startingPlayerIndex = winningPlayerIndex
        return ("${players[winningPlayerIndex].name} has taken a trick with ${gameRound.playedCards[gameRound.getWinningPlayer()].toString()}!")
    }

    fun getWinner(players: Array<CardPlayer>): String {
        var indexWinningPlayer = 0
        var tie = false
        val tiedPlayers = mutableListOf<Int>()
        var winnerText = ""

        for (i in 1 until players.size) {
            if (players[i].score > players[indexWinningPlayer].score) {
                indexWinningPlayer = i
                tie = false
            } else if (players[i].score == players[indexWinningPlayer].score) {
                tie = true

                if (!tiedPlayers.contains(indexWinningPlayer)) {
                    tiedPlayers.add(indexWinningPlayer)
                }

                if (!tiedPlayers.contains(i)) {
                    tiedPlayers.add(i)
                }
            }
        }

        if (!tie) {
            winnerText = ("${players[indexWinningPlayer].name} wins!")
        } else {
            winnerText += ("tie between ")
            for (index in tiedPlayers) {
                winnerText += ("${players[index].name} and ")
            }
            winnerText = winnerText.substringBeforeLast("and ")
            winnerText += ("!")
        }
        return (winnerText)
    }

    private fun printAllHands() { // DEBUG
        for (player in players) {
            println("${player.name} ")
            player.hand.printHand()
        }
    }

    private fun printScoreBoard() {
        println("Scoreboard:")
        for (player in players) {
            println("${player.name} - ${player.score}")
        }
        println()
    }

    private fun drawCardFromDeck(count: Int, deckId: String): List<Card> {
        val response = DrawCardResponse.createAsync(count, deckId)
        return response?.cards ?: emptyList()
    }

    companion object {
        fun getNewDeckAsync(): DrawCardResponse? {
            val response = DrawCardResponse.createAsync(0)

            if (response != null) {
                println("Shuffled deck ID: ${response.deckID}")
                println("Remaining: ${response.remaining}")
            }

            return response
        }
    }
}


