// RecommendedExerciseAdapter.kt
package com.example.pr1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ItemRecommendedExerciseBinding

class RecommendedExerciseAdapter(
    private var exercises: List<ExerciseResponse>,
    private val onExerciseClick: (ExerciseResponse) -> Unit
) : RecyclerView.Adapter<RecommendedExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemRecommendedExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int = exercises.size

    fun updateExercises(newExercises: List<ExerciseResponse>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(private val binding: ItemRecommendedExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExerciseClick(exercises[position])
                }
            }
        }

        fun bind(exercise: ExerciseResponse) {
            // Безопасное отображение названия
            val safeName = exercise.name?.takeIf { it.isNotBlank() } ?: "Упражнение"
            binding.exerciseTitle.text = safeName

            // Отображение длительности
            if (exercise.duration > 0) {
                binding.exerciseDuration.text = "${exercise.duration} мин"
            } else {
                binding.exerciseDuration.text = "10 мин"
            }

            // Отображение уровня сложности
            val difficulty = exercise.difficulty?.takeIf { it.isNotBlank() } ?: "Начинающий"
            binding.exerciseDifficulty.text = difficulty
        }
    }
}