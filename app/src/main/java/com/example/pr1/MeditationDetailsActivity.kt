// MeditationDetailsActivity.kt (обновленный)
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

        // Убираем обработчик нажатия на кнопку "Начать"
        // Скрываем кнопку "Начать"
        binding.startMeditationButton.visibility = android.view.View.GONE
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