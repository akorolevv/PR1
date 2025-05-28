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
        private const val TAG = "ExerciseRepository"
        private const val BASE_URL = "http://10.0.2.2:8080/" // Для эмулятора Android
        // Для реального устройства используйте: "http://192.168.X.X:8080/" (ваш IP)
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val exerciseApiService = retrofit.create(ExerciseApiService::class.java)

    fun searchExercises(
        query: String,
        onSuccess: (List<ExerciseResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "Начинаем поиск с запросом: $query")

        val call = if (query.isEmpty()) {
            exerciseApiService.getAllExercises()
        } else {
            exerciseApiService.searchExercisesByQuery(query)
        }

        call.enqueue(object : Callback<List<ExerciseResponse>> {
            override fun onResponse(
                call: Call<List<ExerciseResponse>>,
                response: Response<List<ExerciseResponse>>
            ) {
                Log.d(TAG, "Получен ответ с кодом: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val exercises = response.body()!!
                    Log.d(TAG, "Получено упражнений: ${exercises.size}")
                    onSuccess(exercises)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
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