package com.example.carddeck

class DeckManager {
    private val deck = mutableListOf<Card>()

    init {
        createDeck()
    }

    private fun createDeck() {
        deck.clear()
        for (suit in Card.Suit.values()) {
            for (rank in Card.Rank.values()) {
                deck.add(Card(suit, rank))
            }
        }
    }

    fun shuffle(): List<Card> {
        deck.shuffle()
        return deck.toList()
    }

    fun getDeck(): List<Card> = deck.toList()

    fun reset() {
        createDeck()
    }
}
