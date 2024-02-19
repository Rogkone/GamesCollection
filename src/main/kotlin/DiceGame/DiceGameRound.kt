package DiceGame

data class DiceGameRound(
    val pointSheet: PointSheet = PointSheet(),
    val roll: DiceRoll = DiceRoll(),
    var allowedToWrite:Boolean = true
) {

    fun calcTotals(): DiceGameRound {
        val updatedPointSheet = pointSheet.withUpdatedScores(mapOf(
            "Upper Sum" to pointSheet.calcUpperSum(),
            "Bonus" to pointSheet.calcBonus(),
            "Upper Total" to pointSheet.calcUpperTotal(),
            "Lower Total" to pointSheet.calcLowerTotal(),
            "Total" to (pointSheet.calcLowerTotal() + pointSheet.calcUpperTotal())
        ))
        return this.copy(pointSheet = updatedPointSheet)
    }

    fun isGameComplete(): Boolean {
        return pointSheet.scores.filterKeys { it !in listOf("Upper Sum", "Bonus", "Upper Total", "Lower Total", "Total") }.all { it.value != null }
    }
}
