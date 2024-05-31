package com.example.dash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("player_prefs", Context.MODE_PRIVATE)

        // Find views by their IDs
        val buttonPlay = findViewById<Button>(R.id.buttonPlay)
        val textViewPlayerName = findViewById<TextView>(R.id.textViewPlayerName)
        val textViewScore = findViewById<TextView>(R.id.textViewScore)
        val textViewHighScore = findViewById<TextView>(R.id.textViewHighScore)

        // Retrieve player's name, score, and high score from SharedPreferences
        val playerName = sharedPreferences.getString("PLAYER_NAME", "")
        val score = sharedPreferences.getInt("score", 0)
        val highScore = sharedPreferences.getInt("high_score", 0)

        // Set player's name, score, and high score on respective TextViews
        textViewPlayerName.text = "Welcome $playerName"
        textViewScore.text = "Score: $score"
        textViewHighScore.text = "High Score: $highScore"

        // Set OnClickListener for Play button
        buttonPlay.setOnClickListener {
            val intent = Intent(this, RunActivity::class.java)
            startActivity(intent)
        }
    }
}
