package com.gooseco.gotchbible

import android.content.Context
import org.json.JSONArray

object WorkoutStorage {
    private const val PREFS_NAME = "GotchBible"
    private const val WORKOUTS_KEY = "workouts"
    private const val MAX_WORKOUTS = 10

    fun saveWorkout(context: Context, workout: WorkoutRecord) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val workouts = getWorkouts(context).toMutableList()

        // Add new workout
        workouts.add(workout)

        // Sort by duration (fastest first) and keep only top 10
        val topWorkouts = workouts.sortedBy { it.durationMillis }.take(MAX_WORKOUTS)

        // Save to preferences
        val jsonArray = JSONArray()
        topWorkouts.forEach { jsonArray.put(it.toJson()) }

        prefs.edit().putString(WORKOUTS_KEY, jsonArray.toString()).apply()
    }

    fun getWorkouts(context: Context): List<WorkoutRecord> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(WORKOUTS_KEY, null) ?: return emptyList()

        val workouts = mutableListOf<WorkoutRecord>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            try {
                workouts.add(WorkoutRecord.fromJson(jsonArray.getString(i)))
            } catch (e: Exception) {
                // Skip invalid entries
            }
        }

        return workouts
    }
}
