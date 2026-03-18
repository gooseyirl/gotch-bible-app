package com.gooseco.gotchbible

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gooseco.gotchbible.databinding.ActivityPastWorkoutsBinding

class PastWorkoutsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPastWorkoutsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPastWorkoutsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWorkouts()
    }

    override fun onResume() {
        super.onResume()
        loadWorkouts()
    }

    private fun loadWorkouts() {
        val workouts = WorkoutStorage.getWorkouts(this)

        if (workouts.isEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            binding.workoutsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.workoutsRecyclerView.visibility = View.VISIBLE

            val adapter = WorkoutAdapter(workouts) { workout ->
                val intent = Intent(this, WorkoutDetailActivity::class.java)
                intent.putExtra("workout", workout.toJson())
                startActivity(intent)
            }

            binding.workoutsRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.workoutsRecyclerView.adapter = adapter
        }
    }
}
