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

interface UserManagementService {
    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @POST("auth/v1/verify")
    suspend fun verifyOTP(@Body verifyOtpRequest: VerifyOtpRequest): Response<Any>

    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @POST("auth/v1/recover")
    suspend fun recoverPassword(@Body body: Map<String, String>): Response<Any>

    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @POST("change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): Response<Any>

    @Headers("apikey: ${API_KEY}")
    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): List<ProfileDto>

    // ИСПРАВЛЕНО: используем конкретный DTO вместо Map<String, Any?>
    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @PUT("rest/v1/profiles")
    suspend fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdFilter: String,
        @Body body: UpdateProfileDto
    ): Response<Unit>

    // Альтернативный вариант: если нужна частичная замена (PATCH)
    @Headers("apikey: ${API_KEY}", "Content-Type: application/json")
    @PATCH("rest/v1/profiles")
    suspend fun patchProfile(
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdFilter: String,
        @Body body: UpdateProfileDto
    ): Response<Unit>
}