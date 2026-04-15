package com.gooseco.gotchbible

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.gooseco.gotchbible.databinding.ActivityWorkoutDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class WorkoutDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val workoutJson = intent.getStringExtra("workout") ?: return
        val workout = WorkoutRecord.fromJson(workoutJson)

        val minutes = (workout.durationMillis / 1000) / 60
        val seconds = (workout.durationMillis / 1000) % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(workout.timestamp))

        binding.completionTimeText.text = "Completion Time: $timeString"
        binding.completionDateText.text = "Date: $dateString"

        binding.clubsExerciseText.text = workout.clubsExercise
        binding.clubsRepsText.text = "${workout.clubsReps} reps"

        binding.heartsExerciseText.text = workout.heartsExercise
        binding.heartsRepsText.text = "${workout.heartsReps} reps"

        binding.spadesExerciseText.text = workout.spadesExercise
        binding.spadesRepsText.text = "${workout.spadesReps} reps"

        binding.diamondsExerciseText.text = workout.diamondsExercise
        binding.diamondsRepsText.text = "${workout.diamondsReps} reps"
    }
}
