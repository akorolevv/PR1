// app/src/main/java/com/example/pr1/ProfileFragment.kt
package com.example.pr1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pr1.data.UserManager
import com.example.pr1.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())

        setupUserInfo()
        setupClickListeners()
        setupThemeSwitcher()
    }

    private fun setupUserInfo() {
        val currentUser = userManager.getCurrentUser()
        if (currentUser != null) {
            binding.profileName.text = currentUser.login
            binding.profileEmail.text = currentUser.email
            binding.profileStatus.text = currentUser.status
        } else {
            binding.profileName.text = "Пользователь"
            binding.profileEmail.text = "user@example.com"
            binding.profileStatus.text = "Новичок в медитации 🧘‍♀️"
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }

        binding.favoritesButton.setOnClickListener {
            // Переход на экран избранного
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        }

        // НОВЫЙ ОБРАБОТЧИК: Редактирование статуса
        binding.editStatusButton.setOnClickListener {
            showEditStatusDialog()
        }

        // Также можно кликнуть по самому статусу
        binding.profileStatus.setOnClickListener {
            showEditStatusDialog()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Выйти") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun performLogout() {
        userManager.logout()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // НОВЫЙ МЕТОД: Показ диалога редактирования статуса
    private fun showEditStatusDialog() {
        val currentStatus = binding.profileStatus.text.toString()

        val editText = EditText(requireContext()).apply {
            setText(currentStatus)
            setSelection(currentStatus.length) // Устанавливаем курсор в конец
            hint = "Введите ваш статус (до 255 символов)"
            maxLines = 3
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Редактирование статуса")
            .setMessage("Расскажите о себе или своих целях в медитации")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newStatus = editText.text.toString().trim()
                when {
                    newStatus.isEmpty() -> {
                        Toast.makeText(requireContext(), "Статус не может быть пустым", Toast.LENGTH_SHORT).show()
                    }
                    newStatus.length > 255 -> {
                        Toast.makeText(requireContext(), "Статус не может быть длиннее 255 символов", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        updateUserStatus(newStatus)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // НОВЫЙ МЕТОД: Обновление статуса пользователя
    private fun updateUserStatus(newStatus: String) {
        // Показываем индикатор загрузки (можно добавить ProgressBar)
        binding.editStatusButton.isEnabled = false

        userManager.updateUserStatus(
            newStatus = newStatus,
            onSuccess = { updatedStatus ->
                requireActivity().runOnUiThread {
                    binding.profileStatus.text = updatedStatus
                    binding.editStatusButton.isEnabled = true
                    Toast.makeText(requireContext(), "Статус обновлен!", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { errorMessage ->
                requireActivity().runOnUiThread {
                    binding.editStatusButton.isEnabled = true
                    Toast.makeText(requireContext(), "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun setupThemeSwitcher() {
        // Получаем экземпляр App чтобы работать с темой
        val app = requireActivity().applicationContext as App

        // Устанавливаем начальное положение переключателя в соответствии с текущей темой
        binding.themeSwitcher.isChecked = app.darkTheme

        // Устанавливаем обработчик изменения переключателя
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            app.switchTheme(isChecked)

            // Перезапускаем активность для применения новой темы
            requireActivity().recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем информацию о пользователе при возврате на экран
        setupUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}