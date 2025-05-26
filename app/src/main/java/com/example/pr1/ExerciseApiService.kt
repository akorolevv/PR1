// ExerciseApiService.kt
package com.example.pr1.data.api

import com.example.pr1.data.models.ExerciseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ExerciseApiService {
    @GET("exercises")
    fun getAllExercises(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") apiHost: String
    ): Call<List<ExerciseResponse>>


    @GET("exercises")
    fun searchExercisesByName(
        @Query("name") name: String,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") apiHost: String
    ): Call<List<ExerciseResponse>>
}