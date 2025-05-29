// CategoryExercisesActivity.kt
package com.example.pr1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pr1.data.ExerciseRepository
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ActivityCategoryExercisesBinding

class CategoryExercisesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryExercisesBinding
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var exerciseAdapter: ExerciseAdapter

    companion object {
        const val EXTRA_CATEGORY_NAME = "category_name"
        const val EXTRA_SEARCH_QUERY = "search_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryExercisesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        exerciseRepository = ExerciseRepository(this)

        val categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: "Категория"
        val searchQuery = intent.getStringExtra(EXTRA_SEARCH_QUERY) ?: ""

        setupToolbar(categoryName)
        setupRecyclerView()
        loadExercises(searchQuery)
    }

    private fun setupToolbar(categoryName: String) {
        binding.categoryTitle.text = categoryName
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(emptyList()) { exercise ->
            val intent = Intent(this, ExerciseDetailActivity::class.java).apply {
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

        binding.exercisesRecycler.apply {
            layoutManager = LinearLayoutManager(this@CategoryExercisesActivity)
            adapter = exerciseAdapter
        }
    }

    private fun loadExercises(searchQuery: String) {
        showLoading(true)

        exerciseRepository.searchExercises(
            query = searchQuery,
            onSuccess = { exercises ->
                runOnUiThread {
                    showLoading(false)
                    if (exercises.isEmpty()) {
                        showEmptyState()
                    } else {
                        showExercises(exercises)
                    }
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    showLoading(false)
                    Toast.makeText(this, "Ошибка загрузки: $errorMessage", Toast.LENGTH_LONG).show()
                    showEmptyState()
                }
            }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.exercisesRecycler.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.emptyState.visibility = View.GONE
    }

    private fun showExercises(exercises: List<ExerciseResponse>) {
        binding.exercisesRecycler.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        exerciseAdapter.updateExercises(exercises)
    }

    private fun showEmptyState() {
        binding.exercisesRecycler.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }
}