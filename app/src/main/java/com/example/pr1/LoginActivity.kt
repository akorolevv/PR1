package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.pr1.data.UserManager
import com.example.pr1.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager(this)

        // Если пользователь уже авторизован, переходим в MainActivity
        if (userManager.isLoggedIn()) {
            navigateToMainActivity()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        // Простая валидация
        if (email.isEmpty()) {
            binding.emailLayout.error = "Введите email"
            return
        }

        // Валидация формата email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Введите корректный email"
            return
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Введите пароль"
            return
        }

        // Очищаем предыдущие ошибки
        binding.emailLayout.error = null
        binding.passwordLayout.error = null

        // Отключаем кнопку во время запроса
        binding.loginButton.isEnabled = false
        binding.loginButton.text = "Входим..."

        userManager.loginUser(
            email = email,
            password = password,
            onSuccess = { authResponse ->
                runOnUiThread {
                    Toast.makeText(this, "Добро пожаловать, ${authResponse.user?.login}!", Toast.LENGTH_SHORT)
                        .show()
                    navigateToMainActivity()
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    binding.loginButton.isEnabled = true
                    binding.loginButton.text = getString(R.string.login_action)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}