package CardGame

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardGameViewModel {
    val initDeck = CardGame.getNewDeckAsync()
    val deckId = initDeck!!.deckID
    val deckSize = initDeck!!.remaining
    var trickString = ""
    private val cardCount = 5
    val playerCount = 4
    private val numberOfHumans = 1

    private val _showRulesDialog = MutableStateFlow(false)
    val showRulesDialog: StateFlow<Boolean> = _showRulesDialog.asStateFlow()

    private val _gameState = MutableStateFlow(CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize))
    val gameState: StateFlow<CardGame> = _gameState

    private val _nextButtonText = MutableStateFlow("")
    val nextButtonText: StateFlow<String> = _nextButtonText.asStateFlow()

    private val _currentPlayerName = MutableStateFlow(gameState.value.players[gameState.value.currentPlayerIndex].name)
    val currentPlayerName: StateFlow<String> = _currentPlayerName.asStateFlow()

    private val _winnerText = MutableStateFlow("")
    val winnerText: StateFlow<String> = _winnerText.asStateFlow()

    private val _trickText = MutableStateFlow("")
    val trickText: StateFlow<String> = _trickText.asStateFlow()

    private val _showSelectCardDialog = MutableStateFlow(false)
    val showSelectCardDialog: StateFlow<Boolean> = _showSelectCardDialog.asStateFlow()

    private val _showWrongSuitDialog = MutableStateFlow(false)
    val showWrongSuitDialog: StateFlow<Boolean> = _showWrongSuitDialog.asStateFlow()

    init {
        if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) setNextButtonText("Next Player")
        else setNextButtonText("Play selected card!")
    }

    fun setShowRulesDialog(show: Boolean) {
        _showRulesDialog.value = show
    }

    fun setShowWrongSuitDialog(show: Boolean) {
        _showWrongSuitDialog.value = show
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
        val currentPlayer = gameState.value.players[gameState.value.currentPlayerIndex]
        val currentPlayerCardIndex = currentPlayer.selectedCardIndex

        if (gameState.value.isGameOver()) {
            gameOverAction()
            if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) setNextButtonText("Next Player")
            else setNextButtonText("Play selected card!")
            setCurrentPlayerName(gameState.value.players[gameState.value.currentPlayerIndex].name)
            return
        }

        if (currentPlayerCardIndex == -1
            && gameState.value.gameRound.playedCards.size != gameState.value.players.size
            && !gameState.value.players[gameState.value.currentPlayerIndex].isAI
        ) {
            setShowSelectCardDialog(true) //if no card chosen
        } else if(
            gameState.value.gameRound.playedCards.isNotEmpty()
            && gameState.value.currentPlayerIndex == 0
            && currentPlayer.hand.cards[currentPlayerCardIndex].suit != gameState.value.gameRound.playedCards[0].suit
            && currentPlayer.hand.cards.any { card -> card.suit == gameState.value.gameRound.playedCards[0].suit }
            ) {
            setShowWrongSuitDialog(true)
            return
        }
        else {
            getNextAction()
            gameState.value.players[gameState.value.currentPlayerIndex].selectedCardIndex = -1
        }
        if (gameState.value.isGameOver()) {
            setNextButtonText("Next Round")
            setWinnerText(gameState.value.getWinner(gameState.value.players))
        }
        else if (gameState.value.players[gameState.value.currentPlayerIndex].isAI) {
            setNextButtonText("Next Player")
        } else {
            setNextButtonText("Play selected card!")
        }
        setCurrentPlayerName(gameState.value.players[gameState.value.currentPlayerIndex].name)
    }

    private fun getNextAction() {
        val gameState = this.gameState
        val currentGame = _gameState.value
        if (currentGame.playedRounds == cardCount) {
            gameOverAction()
        } else {
            gameState.value.currentPlayerTurn(gameState.value.players[gameState.value.currentPlayerIndex])
            if (gameState.value.gameRound.playedCards.size == gameState.value.players.size)
                trickString = currentGame.endRound()
        }
    }

    private fun gameOverAction() {
        DrawCardResponse.shuffelDeck(this.deckId)
        trickString = ""
        _gameState.value = CardGame(playerCount, cardCount, numberOfHumans, deckId, deckSize)
    }

}