package com.gooseco.gotchbible

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

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
    private lateinit var summaryScrollView: View
    private lateinit var congratsTitle: TextView
    private lateinit var completionMessage: TextView
    private lateinit var summaryClubsExercise: TextView
    private lateinit var summaryClubsReps: TextView
    private lateinit var summaryHeartsExercise: TextView
    private lateinit var summaryHeartsReps: TextView
    private lateinit var summarySpadesExercise: TextView
    private lateinit var summarySpadesReps: TextView
    private lateinit var summaryDiamondsExercise: TextView
    private lateinit var summaryDiamondsReps: TextView
    private lateinit var instructionText: TextView
    private lateinit var backButton: MaterialButton
    private lateinit var endButton: MaterialButton
    private lateinit var backToMenuButton: MaterialButton
    private lateinit var konfettiView: KonfettiView
    private val deckManager = DeckManager()
    private lateinit var soundPlayer: SoundPlayer

    private var shuffledCards: List<Card> = emptyList()
    private var currentCardIndex = 0
    private var highestCardReached = 0
    private var startTime: Long = 0
    private var pausedElapsedTime: Long = 0
    private var isTimerRunning = false
    private var isCountdownRunning = false
    private var isWorkoutComplete = false
    private var isWorkoutIncomplete = false
    private val handler = Handler(Looper.getMainLooper())

    // Track reps per exercise
    private val repCounts = mutableMapOf<Card.Suit, Int>()
    private val cardsCompleted = mutableSetOf<Int>()
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
        summaryScrollView = findViewById(R.id.summaryScrollView)
        congratsTitle = findViewById(R.id.congratsTitle)
        completionMessage = findViewById(R.id.completionMessage)
        summaryClubsExercise = findViewById(R.id.summaryClubsExercise)
        summaryClubsReps = findViewById(R.id.summaryClubsReps)
        summaryHeartsExercise = findViewById(R.id.summaryHeartsExercise)
        summaryHeartsReps = findViewById(R.id.summaryHeartsReps)
        summarySpadesExercise = findViewById(R.id.summarySpadesExercise)
        summarySpadesReps = findViewById(R.id.summarySpadesReps)
        summaryDiamondsExercise = findViewById(R.id.summaryDiamondsExercise)
        summaryDiamondsReps = findViewById(R.id.summaryDiamondsReps)
        instructionText = findViewById(R.id.instructionText)
        backButton = findViewById(R.id.backButton)
        endButton = findViewById(R.id.endButton)
        backToMenuButton = findViewById(R.id.backToMenuButton)
        konfettiView = findViewById(R.id.konfettiView)

        // Initialize sound player
        soundPlayer = SoundPlayer(this)

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
            if (!isCountdownRunning && !isWorkoutComplete && currentCardIndex < shuffledCards.size) {
                showNextCard()
            }
        }

        backButton.setOnClickListener {
            showPreviousCard()
        }

        endButton.setOnClickListener {
            showEndWorkoutDialog()
        }

        backToMenuButton.setOnClickListener {
            showBackToMenuDialog()
        }
    }

    private fun showMenuScreen() {
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        menuLayout.visibility = View.VISIBLE
        workoutLayout.visibility = View.GONE
    }

    private fun showWorkoutScreen() {
        menuLayout.visibility = View.GONE
        workoutLayout.visibility = View.VISIBLE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startNewDeck() {
        shuffledCards = deckManager.shuffle()
        currentCardIndex = 0
        highestCardReached = 0
        isWorkoutComplete = false
        isWorkoutIncomplete = false

        // Reset rep counts
        repCounts.clear()
        Card.Suit.values().forEach { repCounts[it] = 0 }
        cardsCompleted.clear()

        // Reset UI
        timerText.text = "00:00.0"
        timerText.visibility = View.VISIBLE
        progressText.text = "0 / 52"
        progressText.visibility = View.VISIBLE
        summaryScrollView.visibility = View.GONE
        instructionText.visibility = View.GONE
        exerciseText.visibility = View.GONE
        backButton.visibility = View.VISIBLE
        endButton.visibility = View.VISIBLE

        // Start countdown
        startCountdown()
    }

    private fun startCountdown() {
        isCountdownRunning = true
        cardDisplay.visibility = View.VISIBLE
        cardDisplay.setTextColor(Color.WHITE)

        // Remove card background/border for countdown
        cardDisplay.setBackgroundColor(Color.TRANSPARENT)
        cardDisplay.elevation = 0f

        var count = 3

        fun showCount() {
            if (count > 0) {
                // Play countdown sound
                soundPlayer.playCountdown()

                // Animate countdown number
                cardDisplay.alpha = 0f
                cardDisplay.scaleX = 0.5f
                cardDisplay.scaleY = 0.5f
                cardDisplay.text = count.toString()

                cardDisplay.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .withEndAction {
                        handler.postDelayed({
                            count--
                            showCount()
                        }, 700)
                    }
                    .start()
            } else {
                // Countdown finished, restore card styling and start workout
                isCountdownRunning = false
                cardDisplay.setBackgroundColor(Color.WHITE)
                cardDisplay.elevation = 8f * resources.displayMetrics.density

                startTime = System.currentTimeMillis()
                isTimerRunning = true
                handler.post(timerRunnable)

                exerciseText.visibility = View.VISIBLE
                instructionText.visibility = View.VISIBLE
                instructionText.text = "Tap anywhere to see next card"

                displayCurrentCard()
            }
        }

        showCount()
    }

    private fun showNextCard() {
        if (currentCardIndex < shuffledCards.size - 1) {
            // Track reps for current card only if we haven't counted it yet
            if (!cardsCompleted.contains(currentCardIndex)) {
                val currentCard = shuffledCards[currentCardIndex]
                repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value
                cardsCompleted.add(currentCardIndex)
            }

            // Play click sound
            soundPlayer.playClick()

            currentCardIndex++
            if (currentCardIndex > highestCardReached) {
                highestCardReached = currentCardIndex
            }
            displayCurrentCard()
        } else if (currentCardIndex == shuffledCards.size - 1) {
            // Track reps for the last card only if we haven't counted it yet
            if (!cardsCompleted.contains(currentCardIndex)) {
                val currentCard = shuffledCards[currentCardIndex]
                repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value
                cardsCompleted.add(currentCardIndex)
            }

            // Mark workout as complete and stop timer
            isWorkoutComplete = true
            isTimerRunning = false
            val elapsedMillis = System.currentTimeMillis() - startTime
            val seconds = (elapsedMillis / 1000) % 60
            val minutes = (elapsedMillis / 1000) / 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            // Hide card, exercise, timer, and progress, show summary
            cardDisplay.visibility = View.GONE
            exerciseText.visibility = View.GONE
            instructionText.visibility = View.GONE
            timerText.visibility = View.GONE
            progressText.visibility = View.GONE
            summaryScrollView.visibility = View.VISIBLE
            backButton.visibility = View.GONE
            endButton.visibility = View.GONE

            // Build summary
            val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
            val clubsExercise = prefs.getString("CLUBS", "Push-up") ?: "Push-up"
            val heartsExercise = prefs.getString("HEARTS", "Squat") ?: "Squat"
            val spadesExercise = prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
            val diamondsExercise = prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"

            completionMessage.text = "You completed the deck in $timeString!"

            summaryClubsExercise.text = clubsExercise
            summaryClubsReps.text = "${repCounts[Card.Suit.CLUBS]} reps"

            summaryHeartsExercise.text = heartsExercise
            summaryHeartsReps.text = "${repCounts[Card.Suit.HEARTS]} reps"

            summarySpadesExercise.text = spadesExercise
            summarySpadesReps.text = "${repCounts[Card.Suit.SPADES]} reps"

            summaryDiamondsExercise.text = diamondsExercise
            summaryDiamondsReps.text = "${repCounts[Card.Suit.DIAMONDS]} reps"

            // Play celebration sound and show confetti
            soundPlayer.playCelebration()
            showCelebration()

            // Save workout record
            val workout = WorkoutRecord(
                timestamp = startTime,
                durationMillis = elapsedMillis,
                clubsReps = repCounts[Card.Suit.CLUBS] ?: 0,
                heartsReps = repCounts[Card.Suit.HEARTS] ?: 0,
                spadesReps = repCounts[Card.Suit.SPADES] ?: 0,
                diamondsReps = repCounts[Card.Suit.DIAMONDS] ?: 0,
                clubsExercise = clubsExercise,
                heartsExercise = heartsExercise,
                spadesExercise = spadesExercise,
                diamondsExercise = diamondsExercise
            )
            WorkoutStorage.saveWorkout(this, workout)
        }
    }

    private fun showCelebration() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).max(300),
            position = Position.Relative(0.5, 0.3)
        )

        konfettiView.start(party)
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

        // Format card text with smaller suit symbol
        val cardText = card.toString()
        val spannableString = SpannableString(cardText)
        val rankLength = card.rank.display.length
        spannableString.setSpan(
            RelativeSizeSpan(0.85f),
            rankLength,
            cardText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        cardDisplay.text = spannableString

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

    private fun showPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--
            displayCurrentCard()
        }
    }

    private fun showEndWorkoutDialog() {
        // Pause timer
        isTimerRunning = false
        pausedElapsedTime = System.currentTimeMillis() - startTime

        AlertDialog.Builder(this)
            .setTitle("End Workout")
            .setMessage("Are you sure you want to end your workout? This will not be saved to your workout history.")
            .setPositiveButton("Yes") { _, _ ->
                endWorkoutEarly()
            }
            .setNegativeButton("No") { _, _ ->
                // Resume timer
                startTime = System.currentTimeMillis() - pausedElapsedTime
                isTimerRunning = true
                handler.post(timerRunnable)
            }
            .setCancelable(false)
            .show()
    }

    private fun endWorkoutEarly() {
        // Include current card in the count if not already counted
        if (!cardsCompleted.contains(currentCardIndex)) {
            val currentCard = shuffledCards[currentCardIndex]
            repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value
            cardsCompleted.add(currentCardIndex)
        }

        isWorkoutComplete = true
        isWorkoutIncomplete = true
        val elapsedMillis = pausedElapsedTime
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / 1000) / 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        // Hide card, exercise, timer, and progress, show summary
        cardDisplay.visibility = View.GONE
        exerciseText.visibility = View.GONE
        instructionText.visibility = View.GONE
        timerText.visibility = View.GONE
        progressText.visibility = View.GONE
        summaryScrollView.visibility = View.VISIBLE
        backButton.visibility = View.GONE
        endButton.visibility = View.GONE

        // Build summary
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        val clubsExercise = prefs.getString("CLUBS", "Push-up") ?: "Push-up"
        val heartsExercise = prefs.getString("HEARTS", "Squat") ?: "Squat"
        val spadesExercise = prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
        val diamondsExercise = prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"

        congratsTitle.text = "Workout Ended"
        completionMessage.text = "Time: $timeString • Cards: ${cardsCompleted.size}/${shuffledCards.size}"

        summaryClubsExercise.text = clubsExercise
        summaryClubsReps.text = "${repCounts[Card.Suit.CLUBS]} reps"

        summaryHeartsExercise.text = heartsExercise
        summaryHeartsReps.text = "${repCounts[Card.Suit.HEARTS]} reps"

        summarySpadesExercise.text = spadesExercise
        summarySpadesReps.text = "${repCounts[Card.Suit.SPADES]} reps"

        summaryDiamondsExercise.text = diamondsExercise
        summaryDiamondsReps.text = "${repCounts[Card.Suit.DIAMONDS]} reps"
    }

    private fun showBackToMenuDialog() {
        if (!isWorkoutComplete) {
            // Pause timer
            isTimerRunning = false
            pausedElapsedTime = System.currentTimeMillis() - startTime

            AlertDialog.Builder(this)
                .setTitle("Back to Menu")
                .setMessage("Are you sure you want to go back to the menu? Your current workout will not be saved.")
                .setPositiveButton("Yes") { _, _ ->
                    showMenuScreen()
                }
                .setNegativeButton("No") { _, _ ->
                    // Resume timer
                    startTime = System.currentTimeMillis() - pausedElapsedTime
                    isTimerRunning = true
                    handler.post(timerRunnable)
                }
                .setCancelable(false)
                .show()
        } else {
            showMenuScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
        soundPlayer.release()
    }
}
