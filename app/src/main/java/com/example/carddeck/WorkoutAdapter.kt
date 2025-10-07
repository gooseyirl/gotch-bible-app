package com.example.carddeck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class WorkoutAdapter(
    private val workouts: List<WorkoutRecord>,
    private val onWorkoutClick: (WorkoutRecord) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText: TextView = view.findViewById(R.id.rankText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val dateText: TextView = view.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.rankText.text = (position + 1).toString()

        val minutes = (workout.durationMillis / 1000) / 60
        val seconds = (workout.durationMillis / 1000) % 60
        holder.timeText.text = String.format("%02d:%02d", minutes, seconds)

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        holder.dateText.text = dateFormat.format(Date(workout.timestamp))

        holder.itemView.setOnClickListener {
            onWorkoutClick(workout)
        }
    }

    override fun getItemCount() = workouts.size
}
