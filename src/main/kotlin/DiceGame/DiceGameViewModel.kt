package DiceGame

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiceGameViewModel {
    private val _gameState = MutableStateFlow(DiceGameRound())
    val gameState: StateFlow<DiceGameRound> = _gameState.asStateFlow()

    private val _showNameInputDialog = MutableStateFlow(false)
    val showNameInputDialog: StateFlow<Boolean> = _showNameInputDialog.asStateFlow()

    private val _showWriteFirstDialog = MutableStateFlow(false)
    val showWriteFirstDialog: StateFlow<Boolean> = _showWriteFirstDialog.asStateFlow()

    private val _showRollFirstDialog = MutableStateFlow(false)
    val showRollFirstDialog: StateFlow<Boolean> = _showRollFirstDialog.asStateFlow()

    private val _safeMode = MutableStateFlow(false)
    val safeMode: StateFlow<Boolean> = _safeMode.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun setShowNameInputDialog(show: Boolean) {
        _showNameInputDialog.value = show
    }

    fun setWriteFirstDialog(show: Boolean) {
        _showWriteFirstDialog.value = show
    }

    fun setRollFirstDialog(show: Boolean) {
        _showRollFirstDialog.value = show
    }

    fun setSafeMode(status: Boolean) {
        _safeMode.value = status
    }

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun writeScore(key: String, score: Int) {
        val currentGameRound = _gameState.value
        if (currentGameRound.allowedToWrite) {
            val updatedScores = currentGameRound.pointSheet.scores.toMutableMap().apply {
                this[key] = score
            }
            val updatedPointSheet = currentGameRound.pointSheet.copy(scores = updatedScores)
            val updatedGameRound = currentGameRound.copy(pointSheet = updatedPointSheet, allowedToWrite = false).calcTotals()

            _gameState.value = updatedGameRound
        }
    }

    fun rollDice() {
        val currentState = _gameState.value
        if (currentState.roll.rollCount < 2) {
            val newState = currentState.roll.rollDice()
            _gameState.value = currentState.copy(roll = newState, allowedToWrite = true)
        }

    }

    fun resetDiceOrPrepareNewGame() {
        if (_gameState.value.isGameComplete()) {
            _gameState.value = DiceGameRound()
        } else if (_gameState.value.roll.rollCount >= 2 && _gameState.value.allowedToWrite) {
            setWriteFirstDialog(true)
        } else {
            val newRoll = DiceRoll()
            _gameState.value = _gameState.value.copy(roll = newRoll, allowedToWrite = true)
        }
    }

    fun toggleDiceReRoll(index: Int) {
        val currentRoll = _gameState.value.roll
        val updatedDice = currentRoll.dice.toMutableList().apply {
            this[index] = this[index].copy(reRoll = !this[index].reRoll)
        }
        _gameState.value = _gameState.value.copy(roll = currentRoll.copy(dice = updatedDice))
    }

}
