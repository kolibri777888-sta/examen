package com.example.examen.data.service

import com.example.examen.data.model.*
import retrofit2.Response
import retrofit2.http.*

const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml6c3dqamR4aHN4dndwYnZ2aWNjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI0NDY2NjYsImV4cCI6MjA4ODAyMjY2Nn0.2jYBR7I2luHVGT0rFdxSSSioNv16i-flg86xcKuQFDs"

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
}