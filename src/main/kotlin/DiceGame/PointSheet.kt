package DiceGame

data class PointSheet (
    val scores: MutableMap<String, Int?> = mutableMapOf(
        "One" to null,
        "Two" to null,
        "Three" to null,
        "Four" to null,
        "Five" to null,
        "Six" to null,
        "Upper Sum" to 0,
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
    ))
{

    fun withUpdatedScores(updates: Map<String, Int?>): PointSheet {
        val newScores = scores.toMutableMap().apply {
            updates.forEach { (key, value) -> this[key] = value }
        }
        return this.copy(scores=newScores)
    }

    fun updateScore(key:String, score:Int):PointSheet {
        scores[key] = score
        return this.copy(scores = scores)
    }
    fun calcBonus(): Int {
        val upperTotal = calcUpperSum()
        return if (upperTotal >= 63) 35 else 0
    }

    fun calcUpperSum(): Int {
        val upperKeys = setOf("One", "Two", "Three", "Four", "Five", "Six")
        return scores.filterKeys { it in upperKeys }.values.filterNotNull().sum()
    }

    fun calcUpperTotal(): Int {
        return calcUpperSum() + calcBonus()
    }

    fun calcLowerTotal(): Int {
        val lowerKeys = setOf("1 Pair", "2 Pair", "3 of a kind", "4 of a kind", "Full House", "Sm. Street", "Lg. Street", "Chance", "Yahtzee")
        return scores.filterKeys { it in lowerKeys }.values.filterNotNull().sum()
    }


}