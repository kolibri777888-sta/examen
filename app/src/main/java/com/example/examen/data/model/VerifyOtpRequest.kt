package com.example.examen.data.model

data class VerifyOtpRequest(
    val type: String = "signup",
    val email: String,
    val token: String
)
