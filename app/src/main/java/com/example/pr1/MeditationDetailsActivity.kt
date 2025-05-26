package com.example.pr1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pr1.databinding.ActivityMeditationDetailsBinding

class MeditationDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeditationDetailsBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.favoriteButton.setOnClickListener {
            toggleFavorite()
        }

        binding.startMeditationButton.setOnClickListener {
            // В реальном приложении здесь был бы запуск медитации
            // Показываем заглушку для демонстрации
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            binding.favoriteButton.setImageResource(android.R.drawable.btn_star)
        }
    }
}