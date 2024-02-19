package CardGame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardGameViewModel {
    val initDeck = CardGame.getNewDeckAsync()
    val deckId = initDeck!!.deckID
    val deckSize = initDeck!!.remaining
    var trickString = ""
    val cardCount = 5
    val playerCount = 4
    val numberOfHumans = 1

    private val _gameState = MutableStateFlow(CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize))
    val gameState: StateFlow<CardGame> = _gameState

    private val _nextButtonText = MutableStateFlow("Next")
    val nextButtonText: StateFlow<String> = _nextButtonText.asStateFlow()

    private val _currentPlayerName = MutableStateFlow(gameState.value.players[gameState.value.currentPlayerIndex].name)
    val currentPlayerName: StateFlow<String> = _currentPlayerName.asStateFlow()

    private val _winnerText = MutableStateFlow("")
    val winnerText: StateFlow<String> = _winnerText.asStateFlow()

    private val _showSelectCardDialog = MutableStateFlow(false)
    val showSelectCardDialog: StateFlow<Boolean> = _showSelectCardDialog.asStateFlow()

    fun setShowSelectCardDialog(show: Boolean) {
        _showSelectCardDialog.value = show
    }

    fun setWinnerText(text: String) {
        _winnerText.value = text
    }

    fun setNextButtonText(text: String) {
        _nextButtonText.value = text
    }

    fun setCurrentPlayerName(text: String) {
        _currentPlayerName.value = text
    }


    fun handleNextAction() {
        var gameOverTest by mutableStateOf(false)
        val currentState = _gameState.value
        if (gameState.value.isGameOver()) {
            getNextAction()
            gameOverTest = true
        }
        if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
            getNextAction()
            gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
        } else {
            if (gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex == -1 && gameState.value.gameRound.playedCards.size != gameState.value.players.size) {
                setShowSelectCardDialog(true) //if no card chosen
            } else {
                getNextAction()
                gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
            }
        }
        if (gameOverTest) {
            setNextButtonText("Next Round")
            gameOverTest = false
        } else if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
            setNextButtonText("Next Player")
        } else {
            setNextButtonText("Play selected card!")
        }
        setCurrentPlayerName(gameState.value.players[gameState.value.currentPlayerIndex].name)
    }

    fun getNextAction() {
        val gameState = this.gameState
        val currentGame = _gameState.value
        if (gameState.value.playedRounds == cardCount) {
            DrawCardResponse.shuffelDeck(this.deckId)
            trickString = ""
            _gameState.value = CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize)
        } else {
            gameState.value.currentPlayerTurn(gameState.value.players[gameState.value.currentPlayerIndex])
            //currentGame.endRound()
            if (gameState.value.gameRound.playedCards.size == gameState.value.players.size)
                trickString = currentGame.endRound()
        }


    }

}