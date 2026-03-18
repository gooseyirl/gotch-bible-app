package com.gooseco.gotchbible

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gooseco.gotchbible.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                binding.timerText.text = String.format("%02d:%02d.%d", minutes, seconds, deciseconds)
                handler.postDelayed(this, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundPlayer = SoundPlayer(this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.startButton.setOnClickListener {
            showWorkoutScreen()
            startNewDeck()
        }

        binding.pastWorkoutsButton.setOnClickListener {
            startActivity(Intent(this, PastWorkoutsActivity::class.java))
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.pageCorner.setOnClickListener {
            startActivity(Intent(this, DonationActivity::class.java))
        }

        binding.workoutLayout.setOnClickListener {
            if (!isCountdownRunning && !isWorkoutComplete && currentCardIndex < shuffledCards.size) {
                showNextCard()
            }
        }

        binding.backButton.setOnClickListener {
            showPreviousCard()
        }

        binding.endButton.setOnClickListener {
            showEndWorkoutDialog()
        }

        binding.backToMenuButton.setOnClickListener {
            showBackToMenuDialog()
        }
    }

    private fun showMenuScreen() {
        isTimerRunning = false
        handler.removeCallbacks(timerRunnable)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.menuLayout.visibility = View.VISIBLE
        binding.workoutLayout.visibility = View.GONE
        binding.pageCorner.visibility = View.VISIBLE
    }

    private fun showWorkoutScreen() {
        binding.menuLayout.visibility = View.GONE
        binding.workoutLayout.visibility = View.VISIBLE
        binding.pageCorner.visibility = View.GONE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startNewDeck() {
        shuffledCards = deckManager.shuffle()
        currentCardIndex = 0
        highestCardReached = 0
        isWorkoutComplete = false
        isWorkoutIncomplete = false

        repCounts.clear()
        Card.Suit.values().forEach { repCounts[it] = 0 }
        cardsCompleted.clear()

        binding.timerText.text = "00:00.0"
        binding.timerText.visibility = View.VISIBLE
        binding.progressText.text = "0 / 52"
        binding.progressText.visibility = View.VISIBLE
        binding.summaryScrollView.visibility = View.GONE
        binding.instructionText.visibility = View.GONE
        binding.exerciseText.visibility = View.GONE
        binding.backButton.visibility = View.VISIBLE
        binding.endButton.visibility = View.VISIBLE

        startCountdown()
    }

    private fun startCountdown() {
        isCountdownRunning = true
        binding.cardDisplay.visibility = View.VISIBLE
        binding.cardDisplay.setTextColor(Color.WHITE)
        binding.cardDisplay.setBackgroundColor(Color.TRANSPARENT)
        binding.cardDisplay.elevation = 0f

        var count = 3

        fun showCount() {
            if (count > 0) {
                soundPlayer.playCountdown()

                binding.cardDisplay.alpha = 0f
                binding.cardDisplay.scaleX = 0.5f
                binding.cardDisplay.scaleY = 0.5f
                binding.cardDisplay.text = count.toString()

                binding.cardDisplay.animate()
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
                isCountdownRunning = false
                binding.cardDisplay.setBackgroundColor(Color.WHITE)
                binding.cardDisplay.elevation = 8f * resources.displayMetrics.density

                startTime = System.currentTimeMillis()
                isTimerRunning = true
                handler.post(timerRunnable)

                binding.exerciseText.visibility = View.VISIBLE
                binding.instructionText.visibility = View.VISIBLE
                binding.instructionText.text = "Tap anywhere to see next card"

                displayCurrentCard()
            }
        }

        showCount()
    }

    private fun showNextCard() {
        if (currentCardIndex < shuffledCards.size - 1) {
            if (!cardsCompleted.contains(currentCardIndex)) {
                val currentCard = shuffledCards[currentCardIndex]
                repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value
                cardsCompleted.add(currentCardIndex)
            }

            soundPlayer.playClick()

            currentCardIndex++
            if (currentCardIndex > highestCardReached) {
                highestCardReached = currentCardIndex
            }
            displayCurrentCard()
        } else if (currentCardIndex == shuffledCards.size - 1) {
            if (!cardsCompleted.contains(currentCardIndex)) {
                val currentCard = shuffledCards[currentCardIndex]
                repCounts[currentCard.suit] = repCounts[currentCard.suit]!! + currentCard.rank.value
                cardsCompleted.add(currentCardIndex)
            }

            isWorkoutComplete = true
            isTimerRunning = false
            val elapsedMillis = System.currentTimeMillis() - startTime
            val seconds = (elapsedMillis / 1000) % 60
            val minutes = (elapsedMillis / 1000) / 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            binding.cardDisplay.visibility = View.GONE
            binding.exerciseText.visibility = View.GONE
            binding.instructionText.visibility = View.GONE
            binding.timerText.visibility = View.GONE
            binding.progressText.visibility = View.GONE
            binding.summaryScrollView.visibility = View.VISIBLE
            binding.backButton.visibility = View.GONE
            binding.endButton.visibility = View.GONE

            val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
            val clubsExercise = prefs.getString("CLUBS", "Push-up") ?: "Push-up"
            val heartsExercise = prefs.getString("HEARTS", "Squat") ?: "Squat"
            val spadesExercise = prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
            val diamondsExercise = prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"

            binding.completionMessage.text = "You completed the deck in $timeString!"

            binding.summaryClubsExercise.text = clubsExercise
            binding.summaryClubsReps.text = "${repCounts[Card.Suit.CLUBS]} reps"

            binding.summaryHeartsExercise.text = heartsExercise
            binding.summaryHeartsReps.text = "${repCounts[Card.Suit.HEARTS]} reps"

            binding.summarySpadesExercise.text = spadesExercise
            binding.summarySpadesReps.text = "${repCounts[Card.Suit.SPADES]} reps"

            binding.summaryDiamondsExercise.text = diamondsExercise
            binding.summaryDiamondsReps.text = "${repCounts[Card.Suit.DIAMONDS]} reps"

            soundPlayer.playCelebration()
            showCelebration()

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
        binding.konfettiView.start(party)
    }

    private fun displayCurrentCard() {
        val card = shuffledCards[currentCardIndex]

        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        val exerciseName = when (card.suit) {
            Card.Suit.CLUBS -> prefs.getString("CLUBS", "Push-up") ?: "Push-up"
            Card.Suit.HEARTS -> prefs.getString("HEARTS", "Squat") ?: "Squat"
            Card.Suit.SPADES -> prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
            Card.Suit.DIAMONDS -> prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"
        }

        binding.exerciseText.text = "$exerciseName x ${card.rank.value}"

        binding.cardDisplay.alpha = 0f
        binding.cardDisplay.scaleX = 0.8f
        binding.cardDisplay.scaleY = 0.8f

        val cardText = card.toString()
        val spannableString = SpannableString(cardText)
        val rankLength = card.rank.display.length
        spannableString.setSpan(
            RelativeSizeSpan(0.85f),
            rankLength,
            cardText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.cardDisplay.text = spannableString

        val textColor = when (card.suit.color) {
            Card.CardColor.RED -> Color.RED
            Card.CardColor.BLACK -> Color.BLACK
        }
        binding.cardDisplay.setTextColor(textColor)

        binding.progressText.text = "Card ${currentCardIndex + 1} of ${shuffledCards.size}"

        binding.cardDisplay.animate()
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
        isTimerRunning = false
        pausedElapsedTime = System.currentTimeMillis() - startTime

        AlertDialog.Builder(this)
            .setTitle("End Workout")
            .setMessage("Are you sure you want to end your workout? This will not be saved to your workout history.")
            .setPositiveButton("Yes") { _, _ -> endWorkoutEarly() }
            .setNegativeButton("No") { _, _ ->
                startTime = System.currentTimeMillis() - pausedElapsedTime
                isTimerRunning = true
                handler.post(timerRunnable)
            }
            .setCancelable(false)
            .show()
    }

    private fun endWorkoutEarly() {
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

        binding.cardDisplay.visibility = View.GONE
        binding.exerciseText.visibility = View.GONE
        binding.instructionText.visibility = View.GONE
        binding.timerText.visibility = View.GONE
        binding.progressText.visibility = View.GONE
        binding.summaryScrollView.visibility = View.VISIBLE
        binding.backButton.visibility = View.GONE
        binding.endButton.visibility = View.GONE

        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        val clubsExercise = prefs.getString("CLUBS", "Push-up") ?: "Push-up"
        val heartsExercise = prefs.getString("HEARTS", "Squat") ?: "Squat"
        val spadesExercise = prefs.getString("SPADES", "Sit-up") ?: "Sit-up"
        val diamondsExercise = prefs.getString("DIAMONDS", "Burpee") ?: "Burpee"

        binding.congratsTitle.text = "Workout Ended"
        binding.completionMessage.text = "Time: $timeString • Cards: ${cardsCompleted.size}/${shuffledCards.size}"

        binding.summaryClubsExercise.text = clubsExercise
        binding.summaryClubsReps.text = "${repCounts[Card.Suit.CLUBS]} reps"

        binding.summaryHeartsExercise.text = heartsExercise
        binding.summaryHeartsReps.text = "${repCounts[Card.Suit.HEARTS]} reps"

        binding.summarySpadesExercise.text = spadesExercise
        binding.summarySpadesReps.text = "${repCounts[Card.Suit.SPADES]} reps"

        binding.summaryDiamondsExercise.text = diamondsExercise
        binding.summaryDiamondsReps.text = "${repCounts[Card.Suit.DIAMONDS]} reps"
    }

    private fun showBackToMenuDialog() {
        if (!isWorkoutComplete) {
            isTimerRunning = false
            pausedElapsedTime = System.currentTimeMillis() - startTime

            AlertDialog.Builder(this)
                .setTitle("Back to Menu")
                .setMessage("Are you sure you want to go back to the menu? Your current workout will not be saved.")
                .setPositiveButton("Yes") { _, _ -> showMenuScreen() }
                .setNegativeButton("No") { _, _ ->
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
