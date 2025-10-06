package com.example.carddeck

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var cardDisplay: TextView
    private lateinit var timerText: TextView
    private lateinit var progressText: TextView
    private lateinit var instructionText: TextView
    private lateinit var shuffleButton: MaterialButton
    private val deckManager = DeckManager()

    private var shuffledCards: List<Card> = emptyList()
    private var currentCardIndex = 0
    private var startTime: Long = 0
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                val elapsedMillis = System.currentTimeMillis() - startTime
                val seconds = (elapsedMillis / 1000) % 60
                val minutes = (elapsedMillis / 1000) / 60
                val deciseconds = (elapsedMillis / 100) % 10
                timerText.text = String.format("%02d:%02d.%d", minutes, seconds, deciseconds)
                handler.postDelayed(this, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        cardDisplay = findViewById(R.id.cardDisplay)
        timerText = findViewById(R.id.timerText)
        progressText = findViewById(R.id.progressText)
        instructionText = findViewById(R.id.instructionText)
        shuffleButton = findViewById(R.id.shuffleButton)

        setupClickListeners()
        startNewDeck()
    }

    private fun setupClickListeners() {
        rootLayout.setOnClickListener {
            if (currentCardIndex < shuffledCards.size) {
                showNextCard()
            }
        }

        shuffleButton.setOnClickListener {
            startNewDeck()
        }
    }

    private fun startNewDeck() {
        shuffledCards = deckManager.shuffle()
        currentCardIndex = 0
        startTime = System.currentTimeMillis()
        isTimerRunning = true
        timerText.text = "00:00.0"
        handler.post(timerRunnable)
        displayCurrentCard()
        instructionText.visibility = View.VISIBLE
    }

    private fun showNextCard() {
        if (currentCardIndex < shuffledCards.size - 1) {
            currentCardIndex++
            displayCurrentCard()
        } else if (currentCardIndex == shuffledCards.size - 1) {
            // Show last card and stop timer
            isTimerRunning = false
            instructionText.text = "Complete! Tap Start Over to try again"
        }
    }

    private fun displayCurrentCard() {
        val card = shuffledCards[currentCardIndex]
        cardDisplay.text = card.toString()

        // Set card color
        val textColor = when (card.suit.color) {
            Card.CardColor.RED -> Color.RED
            Card.CardColor.BLACK -> Color.BLACK
        }
        cardDisplay.setTextColor(textColor)

        progressText.text = "Card ${currentCardIndex + 1} of ${shuffledCards.size}"
    }

    override fun onDestroy() {
        super.onDestroy()
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
    }
}
