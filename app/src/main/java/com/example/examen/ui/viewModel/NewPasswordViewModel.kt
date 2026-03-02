package com.example.examen.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.examen.data.RetrofitInstance
import com.example.examen.data.model.ChangePasswordRequest
import kotlinx.coroutines.launch

class NewPasswordViewModel : ViewModel() {

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun changePassword(email: String, newPassword: String, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                val body = ChangePasswordRequest(email, newPassword)
                val response = RetrofitInstance.userManagementService.changePassword(body)

                if (response.isSuccessful) {
                    // Пароль успешно сменён – отправляем на экран входа
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    errorMessage.value = "Ошибка: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
