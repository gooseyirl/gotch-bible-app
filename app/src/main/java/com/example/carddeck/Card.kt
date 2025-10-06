package com.example.carddeck

data class Card(
    val suit: Suit,
    val rank: Rank
) {
    enum class Suit(val symbol: String, val color: CardColor) {
        HEARTS("♥", CardColor.RED),
        DIAMONDS("♦", CardColor.RED),
        CLUBS("♣", CardColor.BLACK),
        SPADES("♠", CardColor.BLACK)
    }

    enum class Rank(val display: String) {
        ACE("A"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K")
    }

    enum class CardColor {
        RED, BLACK
    }

    override fun toString(): String = "${rank.display}${suit.symbol}"
}
