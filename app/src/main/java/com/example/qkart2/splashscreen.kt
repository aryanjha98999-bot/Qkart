package com.example.qkart2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.constraintlayout.widget.ConstraintLayout

@Suppress("DEPRECATION")
class splashscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)

        val logo = findViewById< ImageView>(R.id.image)
        val title = findViewById<TextView>(R.id.title)
        val subtitle = findViewById<TextView>(R.id.subtitle)
        val footer = findViewById<TextView>(R.id.footer)

        logo.alpha = 0f
        logo.scaleX = 0.7f
        logo.scaleY = 0.7f

        title.alpha = 0f
        subtitle.alpha = 0f
        footer.alpha = 0f

        title.translationY = 60f
        subtitle.translationY = 60f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(900)
            .setStartDelay(300)
            .start()
        title.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setStartDelay(900)
            .start()

        subtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setStartDelay(1100)
            .start()

        footer.animate()
            .alpha(1f)
            .setDuration(700)
            .setStartDelay(1400)
            .start()

        Handler().postDelayed({
            startActivity(Intent(this, StartActivity2::class.java))
            finish()
        }, 3000)
    }
}
