package com.example.carddeck

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var menuLayout: LinearLayout
    private lateinit var workoutLayout: ConstraintLayout
    private lateinit var startButton: MaterialButton
    private lateinit var pastWorkoutsButton: MaterialButton
    private lateinit var settingsButton: MaterialButton

    private lateinit var cardDisplay: TextView
    private lateinit var timerText: TextView
    private lateinit var progressText: TextView
    private lateinit var instructionText: TextView
    private lateinit var backToMenuButton: MaterialButton
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

        // Menu views
        menuLayout = findViewById(R.id.menuLayout)
        startButton = findViewById(R.id.startButton)
        pastWorkoutsButton = findViewById(R.id.pastWorkoutsButton)
        settingsButton = findViewById(R.id.settingsButton)

        // Workout views
        workoutLayout = findViewById(R.id.workoutLayout)
        cardDisplay = findViewById(R.id.cardDisplay)
        timerText = findViewById(R.id.timerText)
        progressText = findViewById(R.id.progressText)
        instructionText = findViewById(R.id.instructionText)
        backToMenuButton = findViewById(R.id.backToMenuButton)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener {
            showWorkoutScreen()
            startNewDeck()
        }

        pastWorkoutsButton.setOnClickListener {
            val intent = Intent(this, PastWorkoutsActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        workoutLayout.setOnClickListener {
            if (currentCardIndex < shuffledCards.size) {
                showNextCard()
            }
        }

        backToMenuButton.setOnClickListener {
            showMenuScreen()
        }
    }

    private fun showMenuScreen() {
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
        menuLayout.visibility = View.VISIBLE
        workoutLayout.visibility = View.GONE
    }

    private fun showWorkoutScreen() {
        menuLayout.visibility = View.GONE
        workoutLayout.visibility = View.VISIBLE
    }

    private fun startNewDeck() {
        shuffledCards = deckManager.shuffle()
        currentCardIndex = 0
        startTime = System.currentTimeMillis()
        isTimerRunning = true
        timerText.text = "00:00.0"
        handler.post(timerRunnable)
        displayCurrentCard()
        instructionText.text = "Tap anywhere to see next card"
    }

    private fun showNextCard() {
        if (currentCardIndex < shuffledCards.size - 1) {
            currentCardIndex++
            displayCurrentCard()
        } else if (currentCardIndex == shuffledCards.size - 1) {
            // Show last card and stop timer
            isTimerRunning = false
            instructionText.text = "Complete! Tap Back to Menu"
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
