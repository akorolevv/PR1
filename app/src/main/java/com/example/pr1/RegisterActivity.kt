package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pr1.data.UserManager
import com.example.pr1.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем тему перед вызовом super.onCreate()
        val app = applicationContext as App
        setTheme(if (app.darkTheme) R.style.Theme_MeditationApp_Dark else R.style.Theme_MeditationApp)

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            performRegistration()
        }

        binding.loginLink.setOnClickListener {
            finish()
        }
    }

    private fun performRegistration() {
        val login = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        // Валидация
        var hasErrors = false

        if (login.isEmpty()) {
            binding.nameLayout.error = "Введите логин"
            hasErrors = true
        } else if (login.length < 3) {
            binding.nameLayout.error = "Логин должен содержать минимум 3 символа"
            hasErrors = true
        } else {
            binding.nameLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailLayout.error = "Введите email"
            hasErrors = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Введите корректный email"
            hasErrors = true
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Введите пароль"
            hasErrors = true
        } else if (password.length < 6) {
            binding.passwordLayout.error = "Пароль должен содержать минимум 6 символов"
            hasErrors = true
        } else {
            binding.passwordLayout.error = null
        }

        if (hasErrors) return

        // Отключаем кнопку во время запроса
        binding.registerButton.isEnabled = false
        binding.registerButton.text = "Регистрируемся..."

        userManager.registerUser(
            login = login,
            email = email,
            password = password,
            onSuccess = { authResponse ->
                runOnUiThread {
                    Toast.makeText(this, "Регистрация успешна! Добро пожаловать, ${authResponse.user?.login}!", Toast.LENGTH_SHORT)
                        .show()
                    navigateToOnboarding()
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    binding.registerButton.isEnabled = true
                    binding.registerButton.text = getString(R.string.register_action)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }
}