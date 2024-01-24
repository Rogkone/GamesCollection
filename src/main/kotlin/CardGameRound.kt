class CardGameRound(var trump: String) {
    val playedCards: MutableList<Card> = mutableListOf()
    val playedCardsHash: MutableMap<String, Card> = mutableMapOf()
    fun addCard(name: String, card: Card) {
        playedCards.add(card)
        playedCardsHash.put(name, card)
    }

    fun getWinningPlayer(): Int {
        var index = 0
        var highestCard = playedCards[0]
        if (!checkIfTrumpWasPlayed()) {
            trump = playedCards[0].suit
        }
        for ((i, card) in playedCards.withIndex()) {
            if (card.getRank(trump) > highestCard.getRank(trump)) {
                highestCard = card
                index = i
            }
        }
        return index
    }

    private fun checkIfTrumpWasPlayed(): Boolean {
        for (card in playedCards) {
            if (card.suit == trump) {
                return true
            }
        }
        return false
    }

    companion object {
        fun checkIfTrumpWasPlayed(playedCards: List<Card>, trump: String): Boolean {
            for (card in playedCards) {
                if (card.suit == trump) {
                    return true
                }
            }
            return false
        }
    }
}


