package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        } else {
            binding.profileName.text = "Пользователь"
            binding.profileEmail.text = "user@example.com"
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            // Выходим из аккаунта
            userManager.logout()

            // Переходим на экран логина
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.favoritesButton.setOnClickListener {
            // Переход на экран избранного
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.settingsButton.setOnClickListener {
            // Здесь могла бы быть обработка нажатия на кнопку настроек
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}