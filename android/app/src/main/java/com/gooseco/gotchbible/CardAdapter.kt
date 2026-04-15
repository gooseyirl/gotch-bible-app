package com.gooseco.gotchbible

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private var cards: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardText: TextView = view.findViewById(R.id.cardText)
        val suitText: TextView = view.findViewById(R.id.suitText)
        val rankText: TextView = view.findViewById(R.id.rankText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        holder.cardText.text = card.toString()
        holder.suitText.text = card.suit.symbol
        holder.rankText.text = card.rank.display

        // Set color based on suit
        val color = when (card.suit.color) {
            Card.CardColor.RED -> ContextCompat.getColor(holder.itemView.context, R.color.card_red)
            Card.CardColor.BLACK -> ContextCompat.getColor(holder.itemView.context, R.color.card_black)
        }

        holder.cardText.setTextColor(color)
        holder.suitText.setTextColor(color)
        holder.rankText.setTextColor(color)
    }

    override fun getItemCount(): Int = cards.size

    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }
}
