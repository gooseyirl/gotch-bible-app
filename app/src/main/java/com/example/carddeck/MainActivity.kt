package com.example.carddeck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shuffleButton: MaterialButton
    private lateinit var cardAdapter: CardAdapter
    private val deckManager = DeckManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.cardsRecyclerView)
        shuffleButton = findViewById(R.id.shuffleButton)

        setupRecyclerView()
        setupButtons()

        // Shuffle and display cards on initial load
        shuffleDeck()
    }

    private fun setupRecyclerView() {
        cardAdapter = CardAdapter(emptyList())
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            adapter = cardAdapter
        }
    }

    private fun setupButtons() {
        shuffleButton.setOnClickListener {
            shuffleDeck()
        }
    }

    private fun shuffleDeck() {
        val shuffledCards = deckManager.shuffle()
        cardAdapter.updateCards(shuffledCards)
    }
}
