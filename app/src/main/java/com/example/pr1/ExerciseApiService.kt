// ExerciseApiService.kt
package com.example.pr1.data.api

import com.example.pr1.data.models.ExerciseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseApiService {
    @GET("exercises")
    fun getAllExercises(): Call<List<ExerciseResponse>>

    @GET("exercises/search")
    fun searchExercisesByQuery(
        @Query("query") query: String
    ): Call<List<ExerciseResponse>>
}