package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pr1.data.UserManager
import com.example.pr1.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем тему до вызова super.onCreate и setContentView
        val app = applicationContext as App
        setTheme(if (app.darkTheme) R.style.Theme_MeditationApp_Dark else R.style.Theme_MeditationApp)

        super.onCreate(savedInstanceState)

        // Инициализируем UserManager
        userManager = UserManager(this)

        // Проверяем авторизацию
        if (!userManager.isLoggedIn()) {
            // Если пользователь не авторизован, переходим на экран входа
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // По умолчанию загружаем HomeFragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.navigation_home -> HomeFragment()
            R.id.navigation_search -> SearchFragment()
            R.id.navigation_favorites -> FavoritesFragment()
            R.id.navigation_profile -> ProfileFragment()
            else -> null
        }

        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        fragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
            return true
        }
        return false
    }
}