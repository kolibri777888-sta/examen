package com.example.examen.data.service

import com.example.examen.data.model.*
import retrofit2.Response
import retrofit2.http.*

const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml6c3dqamR4aHN4dndwYnZ2aWNjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI0NDY2NjYsImV4cCI6MjA4ODAyMjY2Nn0.2jYBR7I2luHVGT0rFdxSSSioNv16i-flg86xcKuQFDs"

data class ProfileDto(
    val id: String?,
    val user_id: String?,
    val photo: String?,
    val firstname: String?,
    val lastname: String?,
    val address: String?,
    val phone: String?
)

data class FavouriteDto(
    val id: String?,
    val product_id: String?,
    val user_id: String?
)

data class ProductDto(
    val id: String,
    val title: String,
    val category_id: String?,
    val cost: Double,
    val description: String,
    val is_best_seller: Boolean?
)

interface UserManagementService {

    // ---------- РЕГИСТРАЦИЯ ----------
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    // ---------- ВХОД ----------
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    // ---------- ВОССТАНОВЛЕНИЕ ПАРОЛЯ ----------
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @POST("auth/v1/recover")
    suspend fun recoverPassword(@Body body: Map<String, String>): Response<Any>

    // ---------- ПРОВЕРКА OTP ----------
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @POST("auth/v1/verify")
    suspend fun verifyOTP(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    // ---------- ИЗМЕНЕНИЕ ПАРОЛЯ ----------
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") authHeader: String,
        @Body request: ChangePasswordRequest
    ): Response<Any>

    @Headers("apikey: ${com.example.examen.data.service.API_KEY}")
    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdFilter: String, // "eq.<uuid>"
        @Query("select") select: String = "*"
    ): List<com.example.examen.data.service.ProfileDto>

    @Headers("apikey: ${com.example.examen.data.service.API_KEY}", "Content-Type: application/json")
    @PUT("rest/v1/profiles")
    suspend fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdFilter: String,
        @Body body: Map<String, Any?>
    ): Response<Unit>
}