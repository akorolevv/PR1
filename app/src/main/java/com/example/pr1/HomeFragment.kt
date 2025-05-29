// HomeFragment.kt (обновленный)
package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pr1.data.ExerciseRepository
import com.example.pr1.data.UserManager
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userManager: UserManager
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var recommendedAdapter: RecommendedExerciseAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val categories = listOf(
        Category("Дыхание", android.R.drawable.ic_menu_compass, "дыхание"),
        Category("Растяжка", android.R.drawable.ic_menu_slideshow, "растяжка"),
        Category("Сила", android.R.drawable.ic_menu_view, "сила"),
        Category("Кардио", android.R.drawable.ic_menu_gallery, "кардио")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())
        exerciseRepository = ExerciseRepository(requireContext())

        setupUserGreeting()
        setupDailyMeditation()
        setupRecommendedExercises()
        setupCategories()
        loadRecommendedExercises()
    }

    private fun setupUserGreeting() {
        val currentUser = userManager.getCurrentUser()
        if (currentUser != null) {
            binding.welcomeTitle.text = "Здравствуйте, ${currentUser.login}"
        } else {
            binding.welcomeTitle.text = "Здравствуйте"
        }
    }

    private fun setupDailyMeditation() {
        binding.dailyMeditationCard.setOnClickListener {
            navigateToMeditationDetails()
        }

        binding.startMeditationButton.setOnClickListener {
            navigateToMeditationDetails()
        }
    }

    private fun setupRecommendedExercises() {
        recommendedAdapter = RecommendedExerciseAdapter(emptyList()) { exercise ->
            val intent = Intent(requireContext(), ExerciseDetailActivity::class.java).apply {
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_ID, exercise.id)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_NAME, exercise.name)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_BODY_PART, exercise.bodyPart)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_TARGET, exercise.target)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_EQUIPMENT, exercise.equipment)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DESCRIPTION, exercise.description)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DIFFICULTY, exercise.difficulty)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_DURATION, exercise.duration)
                putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_INSTRUCTIONS, exercise.instructions)
            }
            startActivity(intent)
        }

        binding.recommendedRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = recommendedAdapter
        }
    }

    private fun setupCategories() {
        categoryAdapter = CategoryAdapter(categories) { category ->
            val intent = Intent(requireContext(), CategoryExercisesActivity::class.java).apply {
                putExtra(CategoryExercisesActivity.EXTRA_CATEGORY_NAME, category.name)
                putExtra(CategoryExercisesActivity.EXTRA_SEARCH_QUERY, category.searchQuery)
            }
            startActivity(intent)
        }

        binding.categoriesRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
        }
    }

    private fun loadRecommendedExercises() {
        // Загружаем первые 4 упражнения для рекомендуемых
        exerciseRepository.searchExercises(
            query = "", // Пустой запрос для получения всех упражнений
            onSuccess = { exercises ->
                requireActivity().runOnUiThread {
                    // Берем только первые 4 упражнения
                    val recommendedExercises = exercises.take(4)
                    recommendedAdapter.updateExercises(recommendedExercises)
                }
            },
            onError = { errorMessage ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Ошибка загрузки упражнений: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun navigateToMeditationDetails() {
        val intent = Intent(activity, MeditationDetailsActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}