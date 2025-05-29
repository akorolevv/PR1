package com.example.pr1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Базовый класс для всех активностей с правильной инициализацией темы
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем тему безопасно перед вызовом super.onCreate()
        applyTheme()
        super.onCreate(savedInstanceState)
    }

    private fun applyTheme() {
        try {
            val app = applicationContext as App
            val themeId = if (app.darkTheme) {
                R.style.Theme_MeditationApp_Dark
            } else {
                R.style.Theme_MeditationApp
            }
            setTheme(themeId)
        } catch (e: Exception) {
            // В случае ошибки используем светлую тему по умолчанию
            setTheme(R.style.Theme_MeditationApp)
        }
    }
}