package com.example.examen.ui.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.examen.data.RetrofitInstance
import com.example.examen.data.model.VerifyOtpRequest
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана подтверждения OTP-кода (VerifyOTPScreen)
 * Отвечает за проверку кода подтверждения, отправленного на email пользователя
 * Поддерживает два сценария: регистрация (signup) и восстановление пароля (recovery)
 */
class VerifyOTPViewModel : ViewModel() {

    /**
     * Проверяет введенный пользователем OTP-код
     * В зависимости от типа операции перенаправляет на соответствующий экран
     *
     * @param email email пользователя, на который был отправлен код
     * @param token OTP-код, введенный пользователем
     * @param type тип операции: "signup" для регистрации или "recovery" для восстановления пароля
     * @param context контекст для отображения Toast-уведомлений
     * @param navController навигационный контроллер для перехода на следующий экран
     */
    fun verifyOTP(
        email: String,
        token: String,
        type: String,
        context: Context,
        navController: NavController
    ) {
        // Запускаем корутину в скоупе ViewModel
        viewModelScope.launch {
            try {
                /**
                 * Определяем тип запроса для API
                 * В API могут использоваться другие названия, поэтому преобразуем
                 * "recovery" → "recovery", "signup" → "signup"
                 * (можно добавить дополнительную логику преобразования при необходимости)
                 */
                val requestType = if (type == "recovery") "recovery" else "signup"

                // Создаем тело запроса с email, кодом и типом операции
                val request = VerifyOtpRequest(
                    type = requestType,
                    email = email,
                    token = token
                )

                /**
                 * Отправка POST-запроса на сервер для проверки OTP-кода
                 * Тело запроса: {"type": "signup", "email": "user@example.com", "token": "12345678"}
                 */
                val response = RetrofitInstance.userManagementService.verifyOTP(request)

                // Проверяем успешность ответа (код 2xx)
                if (response.isSuccessful) {
                    // Код подтвержден успешно
                    if (type == "recovery") {
                        /**
                         * Сценарий восстановления пароля
                         * После успешного подтверждения кода перенаправляем на экран
                         * установки нового пароля, передавая email
                         */
                        navController.navigate("new_password/$email")
                    } else {
                        /**
                         * Сценарий регистрации
                         * После успешного подтверждения email перенаправляем на экран входа
                         * Очищаем стек навигации, удаляя экран регистрации,
                         * чтобы пользователь не мог вернуться назад
                         */
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                } else {
                    // Ошибка сервера - неверный код или код истек
                    Toast.makeText(context, "Неверный код", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Обработка сетевых ошибок или других исключений
                Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}