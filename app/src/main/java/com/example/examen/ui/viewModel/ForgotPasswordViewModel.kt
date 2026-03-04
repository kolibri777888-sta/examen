package com.example.examen.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen.data.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана восстановления пароля (ForgotPasswordScreen)
 * Отвечает за отправку email для сброса пароля и управление состоянием диалога
 */
class ForgotPasswordViewModel : ViewModel() {

    /**
     * Состояние диалога подтверждения отправки письма
     * true - показать диалог "Проверьте ваш email"
     * false - скрыть диалог
     */
    val showDialog = mutableStateOf(false)

    /**
     * Сообщение об ошибке при отправке письма
     * null - ошибки нет
     * не null - текст ошибки для отображения
     */
    val errorMessage = mutableStateOf<String?>(null)

    /**
     * Отправляет запрос на восстановление пароля на указанный email
     * При успешной отправке показывает диалог подтверждения
     * При ошибке сохраняет сообщение об ошибке
     *
     * @param email email пользователя для восстановления пароля
     */
    fun sendRecoveryEmail(email: String) {
        // Запускаем корутину в скоупе ViewModel
        // Это гарантирует, что запрос будет отменен при уничтожении ViewModel
        viewModelScope.launch {
            try {
                // Сбрасываем предыдущую ошибку перед новым запросом
                errorMessage.value = null

                /**
                 * Отправка POST-запроса на сервер для восстановления пароля
                 * Тело запроса: {"email": "user@example.com"}
                 */
                val response = RetrofitInstance.userManagementService
                    .recoverPassword(mapOf("email" to email))

                // Проверяем успешность ответа (код 2xx)
                if (response.isSuccessful) {
                    // Письмо успешно отправлено – показываем диалог "Проверьте email"
                    showDialog.value = true
                } else {
                    // Обработка ошибки сервера (код 4xx, 5xx)
                    errorMessage.value = "Ошибка: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                // Обработка ошибок сети или других исключений
                errorMessage.value = "Ошибка сети: ${e.message}"
            }
        }
    }
}