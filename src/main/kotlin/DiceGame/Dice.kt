package DiceGame

data class Dice(
    val value: Int,
    val reRoll: Boolean = true
) {
    val img: String get() = "$value.png"

    fun toggleReRoll(): Dice {
        return this.copy(reRoll = !reRoll)
    }
}


