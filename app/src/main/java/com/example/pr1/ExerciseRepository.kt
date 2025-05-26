// ExerciseRepository.kt
package com.example.pr1.data

import android.content.Context
import android.util.Log
import com.example.pr1.data.api.ExerciseApiService
import com.example.pr1.data.models.ExerciseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExerciseRepository(private val context: Context) {

    companion object {
        private const val API_KEY = "b3ea175a4dmshb8a6c2db5695ee2p173a06jsne4066b3a30d9"
        private const val API_HOST = "exercisedb.p.rapidapi.com"
        private const val TAG = "ExerciseRepository"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://exercisedb.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val exerciseApiService = retrofit.create(ExerciseApiService::class.java)

    // Функция поиска упражнений с обратным вызовом
    fun searchExercises(
        query: String,
        onSuccess: (List<ExerciseResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "Начинаем поиск с запросом: $query")

        // Сначала запрашиваем все упражнения, потом фильтруем локально
        // Это исправит проблему с некорректными результатами
        exerciseApiService.getAllExercises(API_KEY, API_HOST)
            .enqueue(object : Callback<List<ExerciseResponse>> {
                override fun onResponse(
                    call: Call<List<ExerciseResponse>>,
                    response: Response<List<ExerciseResponse>>
                ) {
                    Log.d(TAG, "Получен ответ с кодом: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val allExercises = response.body()!!
                        Log.d(TAG, "Получено всего упражнений: ${allExercises.size}")

                        // Фильтруем упражнения локально по запросу
                        val filteredExercises = allExercises.filter {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.bodyPart.contains(query, ignoreCase = true) ||
                                    it.target.contains(query, ignoreCase = true) ||
                                    it.equipment.contains(query, ignoreCase = true)
                        }

                        Log.d(TAG, "Отфильтровано упражнений: ${filteredExercises.size}")
                        onSuccess(filteredExercises)
                    } else {
                        val errorMsg = "Error: ${response.code()} - ${response.message()}"
                        Log.e(TAG, errorMsg)

                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Error body: $errorBody")

                        onError(errorMsg)
                    }
                }

                override fun onFailure(call: Call<List<ExerciseResponse>>, t: Throwable) {
                    val errorMsg = "Network error: ${t.message}"
                    Log.e(TAG, errorMsg, t)
                    onError(errorMsg)
                }
            })
    }
}