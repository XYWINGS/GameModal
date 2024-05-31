package com.example.dash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RunActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content view to the custom SurfaceView
        setContentView(GameView(this))
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("player_prefs", Context.MODE_PRIVATE)
    }

    // Custom SurfaceView for the game
    inner class GameView(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
        private var isPlaying = false
        private var holder: SurfaceHolder = getHolder()
        private var screenWidth = 0
        private var screenHeight = 0
        private lateinit var road: Bitmap
        private lateinit var redCar: Bitmap
        private lateinit var otherCar: Bitmap
        private var touchX = 0f
        private var otherCarY = FloatArray(4) // Position of the other cars along the y-axis
        private val numCars = 4 // Number of cars
        private var otherCarXArray = FloatArray(numCars)
        private val carSpeeds = FloatArray(numCars) { 5.0f } // Adjust speeds to 5.0 for all cars
        private val carDelays = longArrayOf(4000L, 2000L, 0L, 1000L) // Delays for each car in milliseconds
        private var score = 0 // Score to keep track of points earned
        private var lastPointTime = 0L // Time when the last point was earned
        private val pointInterval = 2000L // Interval to earn a point (in milliseconds)
        private val paint = Paint().apply {
            color = Color.RED
            textSize = 48f
            textAlign = Paint.Align.CENTER
        }

        init {
            holder.addCallback(this)
            // Set touch listener to detect touch events on the SurfaceView
            setOnTouchListener { _, event ->
                touchX = event.x
                true
            }
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            screenWidth = width
            screenHeight = height
            initializeBitmaps()
            startGame()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            stopGame()
        }

        // Method to initialize bitmaps and game objects
        private fun initializeBitmaps() {
            // Load road bitmap
            road = BitmapFactory.decodeResource(resources, R.drawable.road)

            // Load and scale red car bitmap
            redCar = BitmapFactory.decodeResource(resources, R.drawable.redcar).run {
                Bitmap.createScaledBitmap(this, width / 18, height / 18, false) // Smaller scaling factor for redCar
            }

            // Load and scale other car bitmap
            val originalCarsBitmap = BitmapFactory.decodeResource(resources, R.drawable.cars)
            otherCar = Bitmap.createScaledBitmap(originalCarsBitmap, redCar.width, redCar.height, false)

            // Calculate positions of other cars along the x-axis
            val totalWidth = numCars * otherCar.width
            val spaceBetweenCars = (screenWidth - totalWidth - redCar.width) / (numCars - 1)

            otherCarXArray = FloatArray(numCars)

            for (i in 0 until numCars) {
                otherCarXArray[i] = i * (otherCar.width + spaceBetweenCars).toFloat()
                otherCarY[i] = -otherCar.height.toFloat() // Initialize car positions at the top
            }

            // Initialize touchX to the center of the screen
            touchX = (screenWidth - redCar.width) / 2f

            // Adjust touchX to be slightly higher
            touchX -= redCar.width / 4
        }

        // Method to start the game loop
        private fun startGame() {
            isPlaying = true
            lastPointTime = System.currentTimeMillis()
            Thread(this).start()
        }

        // Method to stop the game loop
        private fun stopGame() {
            isPlaying = false
        }

        // Game loop
        override fun run() {
            while (isPlaying) {
                if (holder.surface.isValid) {
                    val canvas = holder.lockCanvas()
                    draw(canvas)
                    moveOtherCars()
                    updateScore()
                    holder.unlockCanvasAndPost(canvas)
                }
                try {
                    Thread.sleep(16)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Method to move other cars and detect collisions
        private fun moveOtherCars() {
            val currentTime = System.currentTimeMillis()

            for (i in 0 until numCars) {
                val elapsedTime = currentTime - carDelays[i]
                val duration = 3500

                val progress = elapsedTime % duration
                val normalizedProgress = progress.toFloat() / duration

                val startY = -otherCar.height.toFloat()
                val endY = screenHeight.toFloat()
                otherCarY[i] = startY + normalizedProgress * (endY - startY)
                val carSpeed = carSpeeds[i]
                otherCarY[i] += carSpeed

                if (otherCarY[i] > screenHeight) {
                    otherCarY[i] = startY
                }

                val redCarRect = Rect(
                    touchX.toInt(),
                    (screenHeight - redCar.height).toInt(),
                    (touchX + redCar.width).toInt(),
                    screenHeight
                )
                val otherCarRect = Rect(
                    otherCarXArray[i].toInt(),
                    otherCarY[i].toInt(),
                    (otherCarXArray[i] + otherCar.width).toInt(),
                    (otherCarY[i] + otherCar.height).toInt()
                )
                if (Rect.intersects(redCarRect, otherCarRect)) {
                    isPlaying = false
                    showGameOverMessage()
                }
            }
        }

        // Method to update the score
        private fun updateScore() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastPointTime >= pointInterval) {
                score++
                lastPointTime = currentTime
            }
        }

        // Method to display game over message, save score, save high score, and navigate to HomeActivity
        private fun showGameOverMessage() {
            post {
                Toast.makeText(context.applicationContext, "Game Over", Toast.LENGTH_LONG).show()
                saveScore()
                saveHighScore()
                navigateToHomeActivity()
            }
        }

        // Method to save the current score
        private fun saveScore() {
            sharedPreferences.edit().putInt("score", score).apply()
        }

        // Method to save the high score
        private fun saveHighScore() {
            val currentHighScore = sharedPreferences.getInt("high_score", 0)
            if (score > currentHighScore) {
                sharedPreferences.edit().putInt("high_score", score).apply()
            }
        }

        // Method to navigate to HomeActivity
        private fun navigateToHomeActivity() {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        // Method to draw the game elements on the canvas
        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            canvas.drawBitmap(road, null, Rect(0, 0, screenWidth, screenHeight), null)

            val carX = touchX - redCar.width / 2 + 65
            canvas.drawBitmap(redCar, carX, (screenHeight - redCar.height).toFloat()-100, null)

            for (i in 0 until numCars) {
                canvas.drawBitmap(otherCar, otherCarXArray[i], otherCarY[i], null)
            }

            canvas.drawText("Score: $score", 200f, 75f, paint)
        }
    }
}
