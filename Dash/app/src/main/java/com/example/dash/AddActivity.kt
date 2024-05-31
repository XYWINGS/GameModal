package com.example.dash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        sharedPreferences = getSharedPreferences("player_prefs", Context.MODE_PRIVATE)

        val editTextPlayerName = findViewById<EditText>(R.id.editTextPlayerName)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val playerName = editTextPlayerName.text.toString()
            if (playerName.isNotBlank()) {
                savePlayerName(playerName)
                Toast.makeText(this, "Player name saved: $playerName", Toast.LENGTH_SHORT).show()

                // Navigate to HomeActivity with player name
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Optionally finish AddActivity if you don't want to keep it in the back stack
            } else {
                Toast.makeText(this, "Please enter a player name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePlayerName(playerName: String) {
        val editor = sharedPreferences.edit()
        editor.putString("PLAYER_NAME", playerName)
        editor.apply()
    }
}
