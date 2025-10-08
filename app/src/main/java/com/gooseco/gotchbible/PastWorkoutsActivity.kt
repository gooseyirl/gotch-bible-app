package com.gooseco.gotchbible

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PastWorkoutsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_workouts)

        recyclerView = findViewById(R.id.workoutsRecyclerView)
        emptyText = findViewById(R.id.emptyText)

        loadWorkouts()
    }

    override fun onResume() {
        super.onResume()
        loadWorkouts()
    }

    private fun loadWorkouts() {
        val workouts = WorkoutStorage.getWorkouts(this)

        if (workouts.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            val adapter = WorkoutAdapter(workouts) { workout ->
                val intent = Intent(this, WorkoutDetailActivity::class.java)
                intent.putExtra("workout", workout.toJson())
                startActivity(intent)
            }

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}
