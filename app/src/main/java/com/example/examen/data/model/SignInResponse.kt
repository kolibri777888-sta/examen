package com.example.examen.data.model

data class SignInResponse(
    val access_token: String,
    val user: UserDto
)

data class UserDto(
    val id: String
)
