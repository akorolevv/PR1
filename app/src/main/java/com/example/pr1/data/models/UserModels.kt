package com.example.pr1.data.models

data class UserRegistrationRequest(
    val login: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val login: String,
    val password: String
)

data class UserResponse(
    val id: Int,
    val login: String,
    val email: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserResponse? = null,
    val token: String? = null
)

data class FavoriteResponse(
    val success: Boolean,
    val message: String
)