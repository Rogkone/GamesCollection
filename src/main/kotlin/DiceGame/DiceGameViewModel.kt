package DiceGame

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiceGameViewModel {
    private val _gameState = MutableStateFlow(DiceGameRound())
    val gameState: StateFlow<DiceGameRound> = _gameState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun setShowDialog(show: Boolean) {
        _showDialog.value = show
    }
    fun setUserName(name: String) {
        _userName.value = name
    }

    fun writeScore(key: String, score: Int) {
        val currentGameRound = _gameState.value
        if (currentGameRound.allowedToWrite) {
            val updatedGameRound = currentGameRound.writeScore(key, score).calcTotals()
            _gameState.value = updatedGameRound.copy(allowedToWrite = false)
        }
        //else error
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
            // enter name
            // write to db
            _gameState.value = DiceGameRound()
        }  else if (_gameState.value.roll.rollCount >= 2 && _gameState.value.allowedToWrite) {
            println("You must write a score before rolling again.")
        }
        else {
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
