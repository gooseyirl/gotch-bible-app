package com.example.carddeck

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class WorkoutDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_detail)

        // Get workout data from intent
        val workoutJson = intent.getStringExtra("workout") ?: return
        val workout = WorkoutRecord.fromJson(workoutJson)

        // Time and date
        val minutes = (workout.durationMillis / 1000) / 60
        val seconds = (workout.durationMillis / 1000) % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(workout.timestamp))

        findViewById<TextView>(R.id.completionTimeText).text = "Completion Time: $timeString"
        findViewById<TextView>(R.id.completionDateText).text = "Date: $dateString"

        // Exercise details
        findViewById<TextView>(R.id.clubsExerciseText).text = workout.clubsExercise
        findViewById<TextView>(R.id.clubsRepsText).text = "${workout.clubsReps} reps"

        findViewById<TextView>(R.id.heartsExerciseText).text = workout.heartsExercise
        findViewById<TextView>(R.id.heartsRepsText).text = "${workout.heartsReps} reps"

        findViewById<TextView>(R.id.spadesExerciseText).text = workout.spadesExercise
        findViewById<TextView>(R.id.spadesRepsText).text = "${workout.spadesReps} reps"

        findViewById<TextView>(R.id.diamondsExerciseText).text = workout.diamondsExercise
        findViewById<TextView>(R.id.diamondsRepsText).text = "${workout.diamondsReps} reps"
    }
}
