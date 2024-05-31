package com.example.dash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Post a delayed action to navigate to AddActivity after two seconds
        Handler().postDelayed({
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
            finish() // Optionally finish MainActivity if you don't want to keep it in the back stack
        }, 2000) // Delay in milliseconds (2000 milliseconds = 2 seconds)
    }
}
