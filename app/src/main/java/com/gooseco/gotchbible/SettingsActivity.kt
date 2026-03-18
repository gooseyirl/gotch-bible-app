package com.gooseco.gotchbible

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gooseco.gotchbible.databinding.ActivitySettingsBinding
import com.google.android.material.textfield.TextInputLayout

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInputBackgrounds()
        loadExercises()

        binding.saveButton.setOnClickListener {
            saveExercises()
        }
    }

    private fun applyInputBackgrounds() {
        val backgroundColor = ColorStateList.valueOf(Color.parseColor("#363738"))
        binding.clubsInputLayout.setBoxBackgroundColorStateList(backgroundColor)
        binding.heartsInputLayout.setBoxBackgroundColorStateList(backgroundColor)
        binding.spadesInputLayout.setBoxBackgroundColorStateList(backgroundColor)
        binding.diamondsInputLayout.setBoxBackgroundColorStateList(backgroundColor)
    }

    private fun loadExercises() {
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        binding.clubsExerciseInput.setText(prefs.getString("CLUBS", "Push-up"))
        binding.heartsExerciseInput.setText(prefs.getString("HEARTS", "Squat"))
        binding.spadesExerciseInput.setText(prefs.getString("SPADES", "Sit-up"))
        binding.diamondsExerciseInput.setText(prefs.getString("DIAMONDS", "Burpee"))
    }

    private fun saveExercises() {
        val prefs = getSharedPreferences("GotchBible", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("CLUBS", binding.clubsExerciseInput.text.toString())
            putString("HEARTS", binding.heartsExerciseInput.text.toString())
            putString("SPADES", binding.spadesExerciseInput.text.toString())
            putString("DIAMONDS", binding.diamondsExerciseInput.text.toString())
            apply()
        }
        Toast.makeText(this, "Exercises saved!", Toast.LENGTH_SHORT).show()
    }
}
