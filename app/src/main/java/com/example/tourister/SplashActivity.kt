package com.example.tourister

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)

        lifecycleScope.launch {
            delay(200)

            val bitmap = lottieAnimationView.drawToBitmap()
            val palette = Palette.from(bitmap).generate()
            val defaultColor = "BLUE".toColorInt()
            val vibrantColor = palette.getVibrantColor(defaultColor)

            delay(4300)


            // After the delay, start MainActivity
            startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply{
                putExtra("DYNAMIC_COLOR", vibrantColor)
            })
            finish() // Close the splash activity
        }

    }
}