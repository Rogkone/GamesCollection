package CardGame

import androidx.compose.runtime.*
import kotlin.random.Random

data class CardGame(
    val numberOfPlayers: Int,
    val numberOfRounds: Int,
    val numberOfHumans: Int,
    val deckId: String,
    val availableCards: Int,
    var trumpCard: Card = DrawCardResponse.createAsync(1, deckId)!!.cards[0]
) {
    val players: Array<CardPlayer>
    val rounds: Array<CardGameRound>
    var playedRounds by mutableStateOf(0)
    var gameRound: CardGameRound = CardGameRound(trumpCard)
    var currentPlayerIndex: Int

    init {
        val rnd = Random
        if (numberOfPlayers * numberOfRounds > availableCards - 1 || numberOfPlayers * numberOfRounds < 2) {
            throw Exception("Invalid player or rounds count")
        }

        players = Array(numberOfPlayers) { i ->
            val isAI = i >= numberOfHumans
            CardPlayer("Player ${i + 1}", isAI)
        }

        for (i in 0 until numberOfPlayers) {
            players[i].hand.setCards(drawCardFromDeck(numberOfRounds, deckId))
        }
        playedRounds = 0
        currentPlayerIndex = rnd.nextInt(players.size)
        gameRound = CardGameRound(trumpCard)
        rounds = Array(numberOfRounds) { gameRound }

    }

    fun isGameOver(): Boolean {
        return playedRounds >= rounds.size
    }

    fun currentPlayerTurn(currentPlayer: CardPlayer) {
        if (!currentPlayer.isAI)
            print("Test")
        val cardIndex = currentPlayer.getPlayerCardIndex(gameRound.playedCards, gameRound.trump.suit)
        gameRound.addCard(currentPlayer.name, currentPlayer.hand.cards[cardIndex])
        currentPlayer.hand.removeCard(currentPlayer.hand.cards[cardIndex])
        if (!currentPlayer.isAI)
            currentPlayer.selectedCardIndex=-1
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
    }

    fun endRound():String {
        val winningPlayerIndex = (gameRound.getWinningPlayer() + currentPlayerIndex) % players.size

        players[winningPlayerIndex].score++
        currentPlayerIndex = winningPlayerIndex
        playedRounds++

        return getTrickMsg(winningPlayerIndex)
    }

    fun getTrickMsg(winningPlayerIndex: Int):String {
        val winningCard = gameRound.playedCards[gameRound.getWinningPlayer()]
        gameRound.playedCards.clear()
        return ("${players[winningPlayerIndex].name} has taken a trick with ${winningCard}!")
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
            winnerText = winnerText.substringBeforeLast(" and ")
            winnerText += ("!")
        }
        return (winnerText)
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


