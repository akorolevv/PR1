package com.example.pr1

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        const val PREFS_NAME = "theme_preferences"
        const val DARK_THEME = "dark_theme"
    }

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        // Загружаем сохраненные настройки темы
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, false)
    }

    fun switchTheme(darkThemeEnabled: Boolean): Boolean {
        // Проверяем, действительно ли тема изменилась
        if (darkTheme == darkThemeEnabled) {
            return false // Тема не изменилась, не нужно ничего делать
        }

        darkTheme = darkThemeEnabled

        // Сохраняем выбранную тему
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, darkThemeEnabled)
            .apply()

        return true // Тема изменилась
    }
}