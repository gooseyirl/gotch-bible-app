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

    enum class Rank(val display: String, val value: Int) {
        ACE("A", 1),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13)
    }

    enum class CardColor {
        RED, BLACK
    }

    override fun toString(): String = "${rank.display}${suit.symbol}"
}
