package com.example.pr1.data.api

import com.example.pr1.data.models.*
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @POST("auth/register")
    fun registerUser(@Body request: UserRegistrationRequest): Call<AuthResponse>

    @POST("auth/login")
    fun loginUser(@Body request: UserLoginRequest): Call<AuthResponse>

    @GET("auth/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserResponse>

    @GET("favorites")
    fun getUserFavorites(@Header("Authorization") token: String): Call<List<ExerciseResponse>>

    @POST("favorites/{exerciseId}")
    fun addToFavorites(
        @Path("exerciseId") exerciseId: Int,
        @Header("Authorization") token: String
    ): Call<FavoriteResponse>

    @DELETE("favorites/{exerciseId}")
    fun removeFromFavorites(
        @Path("exerciseId") exerciseId: Int,
        @Header("Authorization") token: String
    ): Call<FavoriteResponse>

    @GET("favorites/check/{exerciseId}")
    fun checkIfFavorite(
        @Path("exerciseId") exerciseId: Int,
        @Header("Authorization") token: String
    ): Call<Map<String, Boolean>>
}