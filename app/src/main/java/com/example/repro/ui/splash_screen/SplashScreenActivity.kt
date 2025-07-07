package com.example.repro.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.example.repro.ui.auth.LoginActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenDelay: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        // Handler untuk menunda perpindahan ke LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, splashScreenDelay)
    }
}
