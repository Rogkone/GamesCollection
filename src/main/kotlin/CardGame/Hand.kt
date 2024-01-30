package CardGame

class Hand {
    var cards: MutableList<Card> = mutableListOf()

    fun addCard(card: Card) {
        cards.add(card)
    }

    fun removeCard(card: Card) {
        cards.remove(card)
    }

    @JvmName("setCardsList")
    fun setCards(cards: List<Card>) {
        this.cards = cards.toMutableList()
    }

    fun printHand() {
        var i = 1
        println("Karten:")
        for (card in cards) {
            print("[$i] - ")
            card.printCard()
            i++
        }
    }
}


