package com.example.pr1

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val PREFS_NAME = "theme_preferences"
        const val DARK_THEME = "dark_theme"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        // Загружаем сохраненные настройки темы
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, false)

        // Устанавливаем глобальную тему приложения
        applyGlobalTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Сохраняем выбранную тему
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, darkThemeEnabled)
            .apply()

        // Применяем глобальную тему
        applyGlobalTheme(darkThemeEnabled)
    }

    private fun applyGlobalTheme(darkThemeEnabled: Boolean) {
        // Устанавливаем глобальную тему через AppCompatDelegate
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}