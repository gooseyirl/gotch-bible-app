package com.example.carddeck

import android.content.Context
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
    private lateinit var exerciseText: TextView
    private lateinit var summaryText: TextView
    private lateinit var instructionText: TextView
    private lateinit var backToMenuButton: MaterialButton
    private val deckManager = DeckManager()

    private var shuffledCards: List<Card> = emptyList()
    private var currentCardIndex = 0
    private var startTime: Long = 0
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())

    // Track reps per exercise
    private val repCounts = mutableMapOf<Card.Suit, Int>()
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
        exerciseText = findViewById(R.id.exerciseText)
        summaryText = findViewById(R.id.summaryText)
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

        // Reset rep counts
        repCounts.clear()
        Card.Suit.values().forEach { repCounts[it] = 0 }

        // Reset UI
        cardDisplay.visibility = View.VISIBLE
        exerciseText.visibility = View.VISIBLE
        summaryText.visibility = View.GONE
        instructionText.visibility = View.VISIBLE

        displayCurrentCard()
        instructionText.text = "Tap anywhere to see next card"
    }

    private fun showNextCard() {
        if (currentCardIndex < shuffledCards.size - 1) {
            // Track reps for current card before moving to next
            val currentCard = shuffledCards[currentCardIndex]
            repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value

            currentCardIndex++
            displayCurrentCard()
        } else if (currentCardIndex == shuffledCards.size - 1) {
            // Track reps for the last card
            val currentCard = shuffledCards[currentCardIndex]
            repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value

            // Show completion summary and stop timer
            isTimerRunning = false
            val elapsedMillis = System.currentTimeMillis() - startTime
            val seconds = (elapsedMillis / 1000) % 60
            val minutes = (elapsedMillis / 1000) / 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            // Hide card and exercise, show summary
            cardDisplay.visibility = View.GONE
            exerciseText.visibility = View.GONE
            instructionText.visibility = View.GONE
            summaryText.visibility = View.VISIBLE

            // Build summary text
            val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
            val clubsExercise = prefs.getString("CLUBS", "Push-up") ?: "Push-up"
            val heartsExercise = prefs.getString("HEARTS", "Squat") ?: "Squat"
            val spadesExercise = prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
            val diamondsExercise = prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"

            val summary = """
                Congratulations!
                You completed the deck in $timeString!

                Total Reps:
                $clubsExercise: ${repCounts[Card.Suit.CLUBS]}
                $heartsExercise: ${repCounts[Card.Suit.HEARTS]}
                $spadesExercise: ${repCounts[Card.Suit.SPADES]}
                $diamondsExercise: ${repCounts[Card.Suit.DIAMONDS]}
            """.trimIndent()

            summaryText.text = summary
        }
    }

    private fun displayCurrentCard() {
        val card = shuffledCards[currentCardIndex]

        // Get exercise name from preferences
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        val exerciseName = when (card.suit) {
            Card.Suit.CLUBS -> prefs.getString("CLUBS", "Push-up") ?: "Push-up"
            Card.Suit.HEARTS -> prefs.getString("HEARTS", "Squat") ?: "Squat"
            Card.Suit.SPADES -> prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
            Card.Suit.DIAMONDS -> prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"
        }

        // Update exercise text
        exerciseText.text = "$exerciseName x ${card.rank.value}"

        // Animate card transition
        cardDisplay.alpha = 0f
        cardDisplay.scaleX = 0.8f
        cardDisplay.scaleY = 0.8f

        cardDisplay.text = card.toString()

        // Set card color
        val textColor = when (card.suit.color) {
            Card.CardColor.RED -> Color.RED
            Card.CardColor.BLACK -> Color.BLACK
        }
        cardDisplay.setTextColor(textColor)

        progressText.text = "Card ${currentCardIndex + 1} of ${shuffledCards.size}"

        // Fade in and scale animation
        cardDisplay.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
    }
}
