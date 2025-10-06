package com.example.carddeck

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private lateinit var clubsInput: TextInputEditText
    private lateinit var heartsInput: TextInputEditText
    private lateinit var spadesInput: TextInputEditText
    private lateinit var diamondsInput: TextInputEditText
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        clubsInput = findViewById(R.id.clubsExerciseInput)
        heartsInput = findViewById(R.id.heartsExerciseInput)
        spadesInput = findViewById(R.id.spadesExerciseInput)
        diamondsInput = findViewById(R.id.diamondsExerciseInput)
        saveButton = findViewById(R.id.saveButton)

        loadExercises()

        saveButton.setOnClickListener {
            saveExercises()
        }
    }

    private fun loadExercises() {
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        clubsInput.setText(prefs.getString("CLUBS", "Push-up"))
        heartsInput.setText(prefs.getString("HEARTS", "Squat"))
        spadesInput.setText(prefs.getString("SPADES", "Sit-up"))
        diamondsInput.setText(prefs.getString("DIAMONDS", "Burpee"))
    }

    private fun saveExercises() {
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("CLUBS", clubsInput.text.toString())
            putString("HEARTS", heartsInput.text.toString())
            putString("SPADES", spadesInput.text.toString())
            putString("DIAMONDS", diamondsInput.text.toString())
            apply()
        }
        Toast.makeText(this, "Exercises saved!", Toast.LENGTH_SHORT).show()
    }
}
