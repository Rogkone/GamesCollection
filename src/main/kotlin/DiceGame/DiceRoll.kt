package DiceGame

import kotlin.random.Random

data class DiceRoll(
    val dice: List<Dice> = List(5) { Dice(Random.nextInt(1, 7)) }.sortedBy { it.value },
    val rollCount: Int = 0
) {
    fun rollDice(): DiceRoll {
        if (rollCount > 2) {
            return this
        }
        val newDice = dice.map { if (it.reRoll) Dice(Random.nextInt(1, 7)) else it }.sortedBy { it.value }
        return this.copy(dice = newDice, rollCount = rollCount + 1)
    }

    fun calcCurrentScore(key: String): Int {
        when (key) {
            "One" -> return diceSpecificSum(1)
            "Two" -> return diceSpecificSum(2)
            "Three" -> return diceSpecificSum(3)
            "Four" -> return diceSpecificSum(4)
            "Five" -> return diceSpecificSum(5)
            "Six" -> return diceSpecificSum(6)
            "1 Pair" -> return if (isOnePair()) diceSum() else 0
            "2 Pair" -> return if (isTwoPair()) diceSum() else 0
            "3 of a kind" -> return if (isThreeOfAKind()) diceSum() else 0
            "4 of a kind" -> return if (isFourOfAKind()) diceSum() else 0
            "Full House" -> return if (isFullHouse()) 25 else 0
            "Sm. Street" -> return if (isSmallStreet()) 30 else 0
            "Lg. Street" -> return if (isLargeSteet()) 40 else 0
            "Chance" -> return diceSum()
            "Yahtzee" -> return if (isYahtzee()) 50 else 0
        }
        return -1
    }

    fun diceSum(): Int {
        return dice.sumOf { it.value }
    }

    fun diceSpecificSum(value: Int): Int {
        return dice.count { it.value == value } * value
    }

    fun isOnePair(): Boolean {
        for (i in 0 until dice.size - 1) {
            if (dice[i].value == dice[i + 1].value) {
                return true
            }
        }
        return false
    }

    fun isTwoPair(): Boolean {
        var pairCount = 0
        var i = 0
        while (i < dice.size - 1) {
            if (dice[i].value == dice[i + 1].value) {
                pairCount++
                i += 2
            } else {
                i++
            }
        }
        return pairCount == 2
    }

    fun isThreeOfAKind(): Boolean {
        for (i in 0 until dice.size - 2) {
            if (dice[i].value == dice[i + 1].value && dice[i].value == dice[i + 2].value) {
                return true
            }
        }
        return false
    }

    fun isFourOfAKind(): Boolean {
        for (i in 0 until dice.size - 3) {
            if (dice[i].value == dice[i + 1].value && dice[i].value == dice[i + 2].value && dice[i].value == dice[i + 3].value) {
                return true
            }
        }
        return false
    }

    fun isFullHouse(): Boolean {
        return (dice[0].value == dice[1].value && dice[2].value == dice[3].value && dice[2].value == dice[4].value ||
                dice[0].value == dice[1].value && dice[0].value == dice[2].value && dice[3].value == dice[4].value)
    }

    fun isSmallStreet(): Boolean {
        var consecutiveCount = 1
        for (i in 0 until dice.size - 1) {
            when {
                dice[i].value + 1 == dice[i + 1].value -> consecutiveCount++
                dice[i].value == dice[i + 1].value -> continue
                else -> consecutiveCount = 1
            }
            if (consecutiveCount >= 4)
                return true
        }
        return false
    }

    fun isLargeSteet(): Boolean {
        return (dice[0].value == dice[1].value - 1 && dice[0].value == dice[2].value - 2 && dice[0].value == dice[3].value - 3 && dice[0].value == dice[4].value - 4)
    }

    fun isYahtzee(): Boolean {
        return (dice[0].value == dice[1].value && dice[0].value == dice[2].value && dice[0].value == dice[3].value && dice[0].value == dice[4].value)
    }


}
