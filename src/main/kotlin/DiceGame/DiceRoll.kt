package DiceGame

import androidx.compose.runtime.mutableStateListOf
import kotlin.random.Random

class DiceRoll {
    var dice = mutableStateListOf<Dice>()

    init {
        repeat(5) {
            dice.add(Dice(Random.nextInt(1, 7)))
        }
    }

    fun rollDice(reRoll: List<Boolean>) {
        reRoll.forEachIndexed { index, shouldReRoll ->
            if (shouldReRoll) {
                dice[index] = Dice(Random.nextInt(1, 7))
            }
        }
    }
}
