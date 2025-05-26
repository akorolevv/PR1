package com.example.pr1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pr1.data.models.ExerciseResponse
import com.example.pr1.databinding.ItemSearchResultCustomBinding

class ExerciseAdapter(
    private var exercises: List<ExerciseResponse>,
    private val onExerciseClick: (ExerciseResponse) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemSearchResultCustomBinding.inflate(
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

    inner class ExerciseViewHolder(private val binding: ItemSearchResultCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExerciseClick(exercises[position])
                }
            }

            // Улучшаем визуальное представление
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
            binding.meditationTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_primary))
            binding.meditationInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_secondary))
        }

        fun bind(exercise: ExerciseResponse) {
            // Используем capitalize для улучшения отображения названия
            binding.meditationTitle.text = exercise.name.capitalizeWords()

            // Более четкое и структурированное отображение информации
            val bodyPart = exercise.bodyPart.capitalizeWords()
            val target = exercise.target.capitalizeWords()
            binding.meditationInfo.text = "Часть тела: $bodyPart • Цель: $target"
        }

        // Функция для преобразования текста в формат "Каждое Слово С Большой Буквы"
        private fun String.capitalizeWords(): String {
            return this.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
        }
    }
}