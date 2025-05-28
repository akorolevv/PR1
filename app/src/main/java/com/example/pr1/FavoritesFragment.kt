package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pr1.data.UserManager
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())

        setupRecyclerView()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(emptyList()) { exercise ->
            // При клике на упражнение можно открыть детали
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

        binding.favoritesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }
    }

    private fun loadFavorites() {
        // Показываем индикатор загрузки (можно добавить ProgressBar)

        userManager.getFavorites(
            onSuccess = { favorites ->
                requireActivity().runOnUiThread {
                    updateUI(favorites)
                }
            },
            onError = { errorMessage ->
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Ошибка загрузки избранного: $errorMessage", Toast.LENGTH_LONG).show()
                    updateUI(emptyList())
                }
            }
        )
    }

    private fun updateUI(favorites: List<ExerciseResponse>) {
        if (favorites.isEmpty()) {
            binding.emptyFavorites.visibility = View.VISIBLE
            binding.favoritesRecycler.visibility = View.GONE
        } else {
            binding.emptyFavorites.visibility = View.GONE
            binding.favoritesRecycler.visibility = View.VISIBLE
            exerciseAdapter.updateExercises(favorites)
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем избранное при возврате на экран
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}