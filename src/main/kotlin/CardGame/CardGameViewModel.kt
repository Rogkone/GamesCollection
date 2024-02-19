package CardGame

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

    private val _nextButtonText = MutableStateFlow("")
    val nextButtonText: StateFlow<String> = _nextButtonText.asStateFlow()

    private val _currentPlayerName = MutableStateFlow(gameState.value.players[gameState.value.currentPlayerIndex].name)
    val currentPlayerName: StateFlow<String> = _currentPlayerName.asStateFlow()

    private val _winnerText = MutableStateFlow("")
    val winnerText: StateFlow<String> = _winnerText.asStateFlow()

    private val _showSelectCardDialog = MutableStateFlow(false)
    val showSelectCardDialog: StateFlow<Boolean> = _showSelectCardDialog.asStateFlow()

    init {
        if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) setNextButtonText("Next Player")
        else setNextButtonText("Play selected card!")
    }

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

        if (gameState.value.isGameOver()) {
            gameOverAction()
            if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) setNextButtonText("Next Player")
            else setNextButtonText("Play selected card!")
            return
        }

        if (gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex == -1
            && gameState.value.gameRound.playedCards.size != gameState.value.players.size
            && !gameState.value.players[gameState.value.currentPlayerIndex].isAI
        ) {
            setShowSelectCardDialog(true) //if no card chosen
        } else {
            getNextAction()
            gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
        }
        if (gameState.value.isGameOver()) {
            setNextButtonText("Next Round")
        }
        else if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
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
            gameOverAction()
        } else {
            gameState.value.currentPlayerTurn(gameState.value.players[gameState.value.currentPlayerIndex])
            if (gameState.value.gameRound.playedCards.size == gameState.value.players.size)
                trickString = currentGame.endRound()
        }
    }

    fun gameOverAction() {
        DrawCardResponse.shuffelDeck(this.deckId)
        trickString = ""
        _gameState.value = CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize)
    }

}