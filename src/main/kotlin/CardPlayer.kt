class CardPlayer {
    var hand: Hand = Hand()
    var score: Int = 0
    var name: String? = null
    var isAI: Boolean = false

    constructor() {
        hand = Hand()
        score = 0
    }

    constructor(name: String) {
        hand = Hand()
        score = 0
        this.name = name
    }

    constructor(name: String, isAI: Boolean) {
        hand = Hand()
        score = 0
        this.name = name
        this.isAI = isAI
    }

    fun addCard(card: Card) {
        hand.addCard(card)
    }

    /**
     * returns index of the card played by the user
     * @param playedCards List of cards already played to show for human players
     * @param trump Trump suite for AI played cards calc
     * @return returns index of card to be played
     */
    fun getPlayerCardIndex(playedCards: List<Card>, trump: String): Int {
        if (!isAI) {
            var index: Int
            try {
                println("$name:")
                hand.printHand()
                println("Welche Karte willst du spielen?")
                index = readLine()!!.toInt() - 1
                if (index < 0 || index > hand.cards.size) {
                    throw IndexOutOfBoundsException()
                }
                return index
            } catch (e: IndexOutOfBoundsException) {
                println("Bitte eine gültige Zahl eingeben:")
                return getPlayerCardIndex(playedCards, trump)
            }
        } else {
            var secondaryTrump = trump
            if (!CardGameRound.checkIfTrumpWasPlayed(playedCards, trump) && playedCards.isNotEmpty()) {
                secondaryTrump = playedCards[0].suit
            }
            val playedCardValues = mutableListOf<Int>()
            val handCardValues = mutableListOf<Int>()
            for (card in hand.cards) {
                handCardValues.add(card.getRank(trump, secondaryTrump))
            }
            for (card in playedCards) {
                playedCardValues.add(card.getRank(trump, secondaryTrump))
            }
            if (playedCards.isNotEmpty() && handCardValues.maxOrNull()!! > playedCardValues.maxOrNull()!! && handCardValues.size > 1) {
                var secondHighest: Int
                do {
                    secondHighest = handCardValues.sortedDescending().distinct().drop(1).first()
                    if (secondHighest > playedCardValues.maxOrNull()!!) {
                        handCardValues.remove(handCardValues.maxOrNull()!!)
                    }
                } while (secondHighest > playedCardValues.maxOrNull()!! && handCardValues.size > 1)
                // funktioniert nur so, sollte aber auch ohne if ???
                if (secondHighest > playedCardValues.maxOrNull()!!) {
                    return handCardValues.indexOf(secondHighest)
                }
                return handCardValues.indexOf(handCardValues.maxOrNull()!!)
            } else {
                return handCardValues.indexOf(handCardValues.minOrNull()!!)
            }
        }
    }
}


