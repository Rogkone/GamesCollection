package DiceGame

data class DiceGameRound(
    val pointSheet: Map<String, Int?> = mapOf(
        "One" to null,
        "Two" to null,
        "Three" to null,
        "Four" to null,
        "Five" to null,
        "Six" to null,
        "Bonus" to 0,
        "Upper Total" to 0,
        "1 Pair" to null,
        "2 Pair" to null,
        "3 of a kind" to null,
        "4 of a kind" to null,
        "Full House" to null,
        "Sm. Street" to null,
        "Lg. Street" to null,
        "Chance" to null,
        "Yahtzee" to null,
        "Lower Total" to 0,
        "Total" to 0
    ),
    val roll: DiceRoll = DiceRoll()
) {
    fun calcBonus(): Int {
        val upperTotal = calcUpperTotal()
        return if (upperTotal >= 63) 35 else 0
    }

    fun calcUpperTotal(): Int {
        val upperKeys = setOf("One", "Two", "Three", "Four", "Five", "Six")
        return pointSheet.filterKeys { it in upperKeys }.values.filterNotNull().sum()
    }

    fun calcLowerTotal(): Int {
        val lowerKeys = setOf("1 Pair", "2 Pair", "3 of a kind", "4 of a kind", "Full House", "Sm. Street", "Lg. Street", "Chance", "Yahtzee")
        return pointSheet.filterKeys { it in lowerKeys }.values.filterNotNull().sum()
    }

    fun writeScore(key: String, score: Int): DiceGameRound {
        val updatedPointSheet = this.pointSheet.toMutableMap()
        updatedPointSheet[key] = score
        // Return a new instance with the updated pointSheet and any other necessary changes.
        return this.copy(pointSheet = updatedPointSheet)
    }

    fun calcTotals(): DiceGameRound {
        val updatedPointSheet = this.pointSheet.toMutableMap()

        updatedPointSheet["Bonus"] = calcBonus()
        updatedPointSheet["Upper Total"] = calcUpperTotal()
        updatedPointSheet["Lower Total"] = calcLowerTotal()
        updatedPointSheet["Total"] = calcLowerTotal()+calcUpperTotal()
        return this.copy(pointSheet = updatedPointSheet)
    }


    fun isGameComplete(): Boolean {
        return pointSheet.filterKeys { it !in listOf("Bonus", "Upper Total", "Lower Total", "Total") }.all { it.value != null }
    }
}
