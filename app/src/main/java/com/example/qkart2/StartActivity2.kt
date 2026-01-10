package com.example.qkart2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.qkart2.databinding.ActivityStart2Binding

class StartActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityStart2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        binding= ActivityStart2Binding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        binding.buttonNext.setOnClickListener {
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
    }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}


