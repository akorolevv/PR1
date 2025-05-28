// app/src/main/java/com/example/pr1/data/models/UserModels.kt
package com.example.pr1.data.models

data class UserRegistrationRequest(
    val login: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: Int,
    val login: String,
    val email: String,
    val status: String
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

data class UpdateStatusRequest(
    val status: String
)

data class UpdateStatusResponse(
    val success: Boolean,
    val message: String,
    val status: String? = null
)