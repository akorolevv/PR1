package com.example.pr1.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.pr1.data.api.UserApiService
import com.example.pr1.data.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserManager(private val context: Context) {

    companion object {
        private const val TAG = "UserManager"
        private const val PREFS_NAME = "user_preferences"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_EMAIL = "user_email"
        private const val BASE_URL = "http://10.0.2.2:8080/" // Для эмулятора
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userApiService = retrofit.create(UserApiService::class.java)

    // Регистрация пользователя
    fun registerUser(
        login: String,
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = UserRegistrationRequest(login, email, password)

        userApiService.registerUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    if (authResponse.success) {
                        // Сохраняем данные пользователя
                        saveUserData(authResponse)
                        onSuccess(authResponse)
                    } else {
                        onError(authResponse.message)
                    }
                } else {
                    onError("Ошибка регистрации: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e(TAG, "Registration failed", t)
                onError("Ошибка сети: ${t.message}")
            }
        })
    }

    // Вход пользователя
    fun loginUser(
        email: String,  // ИЗМЕНЕНО: параметр email вместо login
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = UserLoginRequest(email, password)  // ИЗМЕНЕНО: используем email

        userApiService.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    if (authResponse.success) {
                        // Сохраняем данные пользователя
                        saveUserData(authResponse)
                        onSuccess(authResponse)
                    } else {
                        onError(authResponse.message)
                    }
                } else {
                    onError("Ошибка входа: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e(TAG, "Login failed", t)
                onError("Ошибка сети: ${t.message}")
            }
        })
    }

    // Сохранение данных пользователя
    private fun saveUserData(authResponse: AuthResponse) {
        val user = authResponse.user
        val token = authResponse.token

        if (user != null && token != null) {
            sharedPrefs.edit().apply {
                putString(KEY_TOKEN, token)
                putInt(KEY_USER_ID, user.id)
                putString(KEY_USER_LOGIN, user.login)
                putString(KEY_USER_EMAIL, user.email)
                apply()
            }
        }
    }

    // Проверка, авторизован ли пользователь
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    // Получение токена
    fun getToken(): String? {
        return sharedPrefs.getString(KEY_TOKEN, null)
    }

    // Получение данных текущего пользователя
    fun getCurrentUser(): UserResponse? {
        val token = getToken()
        val userId = sharedPrefs.getInt(KEY_USER_ID, -1)
        val login = sharedPrefs.getString(KEY_USER_LOGIN, null)
        val email = sharedPrefs.getString(KEY_USER_EMAIL, null)

        return if (token != null && userId != -1 && login != null && email != null) {
            UserResponse(userId, login, email)
        } else {
            null
        }
    }

    // Выход из аккаунта
    fun logout() {
        sharedPrefs.edit().clear().apply()
    }

    // Работа с избранным
    fun addToFavorites(
        exerciseId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token == null) {
            onError("Требуется авторизация")
            return
        }

        userApiService.addToFavorites(exerciseId, "Bearer $token")
            .enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val favoriteResponse = response.body()!!
                        if (favoriteResponse.success) {
                            onSuccess()
                        } else {
                            onError(favoriteResponse.message)
                        }
                    } else {
                        onError("Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Log.e(TAG, "Add to favorites failed", t)
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }

    fun removeFromFavorites(
        exerciseId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token == null) {
            onError("Требуется авторизация")
            return
        }

        userApiService.removeFromFavorites(exerciseId, "Bearer $token")
            .enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val favoriteResponse = response.body()!!
                        if (favoriteResponse.success) {
                            onSuccess()
                        } else {
                            onError(favoriteResponse.message)
                        }
                    } else {
                        onError("Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Log.e(TAG, "Remove from favorites failed", t)
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }

    fun getFavorites(
        onSuccess: (List<ExerciseResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token == null) {
            onError("Требуется авторизация")
            return
        }

        userApiService.getUserFavorites("Bearer $token")
            .enqueue(object : Callback<List<ExerciseResponse>> {
                override fun onResponse(call: Call<List<ExerciseResponse>>, response: Response<List<ExerciseResponse>>) {
                    if (response.isSuccessful && response.body() != null) {
                        onSuccess(response.body()!!)
                    } else {
                        onError("Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ExerciseResponse>>, t: Throwable) {
                    Log.e(TAG, "Get favorites failed", t)
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }

    fun checkIfFavorite(
        exerciseId: Int,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getToken()
        if (token == null) {
            onError("Требуется авторизация")
            return
        }

        userApiService.checkIfFavorite(exerciseId, "Bearer $token")
            .enqueue(object : Callback<Map<String, Boolean>> {
                override fun onResponse(call: Call<Map<String, Boolean>>, response: Response<Map<String, Boolean>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val isFavorite = response.body()?.get("isFavorite") ?: false
                        onSuccess(isFavorite)
                    } else {
                        onError("Ошибка: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                    Log.e(TAG, "Check favorite failed", t)
                    onError("Ошибка сети: ${t.message}")
                }
            })
    }
}