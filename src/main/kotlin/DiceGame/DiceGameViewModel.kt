package DiceGame

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class DiceGameViewModel {
    private val _gameState = MutableStateFlow(DiceGameRound())
    val gameState: StateFlow<DiceGameRound> = _gameState.asStateFlow()

    fun rollDice() {
        val newState = _gameState.value.roll.rollDice()
        _gameState.value = _gameState.value.copy(roll = newState)
    }

    fun writeScore(key: String, score: Int) {
        val currentGameRound = _gameState.value
        val updatedGameRound = currentGameRound.writeScore(key, score)
        _gameState.value = updatedGameRound.calcTotals()
    }


    fun resetDiceOrPrepareNewGame() {
        if (_gameState.value.isGameComplete()) {
            _gameState.value = DiceGameRound()
        } else {
            val newRoll = DiceRoll()
            _gameState.value = _gameState.value.copy(roll = newRoll)
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
