package com.example.examen.data.model

data class ChangePasswordRequest(
    val email: String,
    val newPassword: String
)
