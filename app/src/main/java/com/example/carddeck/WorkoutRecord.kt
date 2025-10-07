package com.example.carddeck

import org.json.JSONObject

data class WorkoutRecord(
    val timestamp: Long,
    val durationMillis: Long,
    val clubsReps: Int,
    val heartsReps: Int,
    val spadesReps: Int,
    val diamondsReps: Int,
    val clubsExercise: String,
    val heartsExercise: String,
    val spadesExercise: String,
    val diamondsExercise: String
) {
    fun toJson(): String {
        val json = JSONObject()
        json.put("timestamp", timestamp)
        json.put("durationMillis", durationMillis)
        json.put("clubsReps", clubsReps)
        json.put("heartsReps", heartsReps)
        json.put("spadesReps", spadesReps)
        json.put("diamondsReps", diamondsReps)
        json.put("clubsExercise", clubsExercise)
        json.put("heartsExercise", heartsExercise)
        json.put("spadesExercise", spadesExercise)
        json.put("diamondsExercise", diamondsExercise)
        return json.toString()
    }

    companion object {
        fun fromJson(jsonString: String): WorkoutRecord {
            val json = JSONObject(jsonString)
            return WorkoutRecord(
                timestamp = json.getLong("timestamp"),
                durationMillis = json.getLong("durationMillis"),
                clubsReps = json.getInt("clubsReps"),
                heartsReps = json.getInt("heartsReps"),
                spadesReps = json.getInt("spadesReps"),
                diamondsReps = json.getInt("diamondsReps"),
                clubsExercise = json.getString("clubsExercise"),
                heartsExercise = json.getString("heartsExercise"),
                spadesExercise = json.getString("spadesExercise"),
                diamondsExercise = json.getString("diamondsExercise")
            )
        }
    }
}
