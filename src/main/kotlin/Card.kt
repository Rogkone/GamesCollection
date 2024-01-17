data class Card(
    var code: String,
    var image: String,
    var images: CardImages,
    var value: String,
    var suit: String
) {
    fun getRank(trump: String, secondaryTrump: String? = null): Int {
        var trumpValue = 0
        var secondaryTrumpValue = 0
        if (suit == trump) {
            trumpValue = 30
        } else if (suit == secondaryTrump) {
            secondaryTrumpValue = 15
        }
        return when (value) {
            "ACE" -> 14 + trumpValue + secondaryTrumpValue
            "KING" -> 13 + trumpValue + secondaryTrumpValue
            "QUEEN" -> 12 + trumpValue + secondaryTrumpValue
            "JACK" -> 11 + trumpValue + secondaryTrumpValue
            else -> {
                val numericValue = value.toIntOrNull()
                numericValue?.plus(trumpValue)?.plus(secondaryTrumpValue) ?: 0
            }
        }
    }

    override fun toString(): String {
        return "$value of $suit"
    }

    fun printCard() {
        println("$value of $suit")
    }
}


