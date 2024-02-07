package DiceGame

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class PointSheetJson(
    val onePair: Int,
    val twoPair: Int,
    val threeOfAKind: Int,
    val fourOfAKind: Int,
    val bonus: Int,
    val chance: Int,
    val five: Int,
    val four: Int,
    val fullHouse: Int,
    val lgStreet: Int,
    val lowerTotal: Int,
    val one: Int,
    val name: String,
    val six: Int,
    val smStreet: Int,
    val three: Int,
    val total: Int,
    val two: Int,
    val upperSum: Int,
    val upperTotal: Int,
    val yahtzee: Int,
    val date: String
) {
}