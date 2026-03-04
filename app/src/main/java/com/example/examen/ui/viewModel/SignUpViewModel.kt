package com.example.examen.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.examen.data.RetrofitInstance
import com.example.examen.data.model.SignUpRequest
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана регистрации (RegisterScreen)
 * Отвечает за создание новой учетной записи пользователя
 */
class SignUpViewModel : ViewModel() {

    /**
     * Индикатор загрузки для отображения прогресса во время выполнения запроса
     * true - выполняется запрос (показываем индикатор на кнопке)
     * false - запрос не выполняется
     */
    val isLoading = mutableStateOf(false)

    /**
     * Сообщение об ошибке при регистрации
     * null - ошибки нет
     * не null - текст ошибки для отображения пользователю
     */
    val errorMessage = mutableStateOf<String?>(null)

    /**
     * Отправляет запрос на регистрацию нового пользователя
     * При успешной регистрации перенаправляет на экран подтверждения OTP
     * При ошибке сохраняет сообщение об ошибке
     *
     * @param email email нового пользователя
     * @param password пароль нового пользователя
     * @param navController навигационный контроллер для перехода на экран OTP
     */
    fun signUp(email: String, password: String, navController: NavController) {
        // Запускаем корутину в скоупе ViewModel
        // Это гарантирует, что запрос будет отменен при уничтожении ViewModel
        viewModelScope.launch {
            try {
                // Устанавливаем состояние загрузки и сбрасываем предыдущую ошибку
                isLoading.value = true
                errorMessage.value = null

                /**
                 * Отправка POST-запроса на сервер для регистрации
                 * Тело запроса: {"email": "user@example.com", "password": "pass123"}
                 */
                val response = RetrofitInstance.userManagementService
                    .signUp(SignUpRequest(email, password))

                // Проверяем успешность ответа (код 2xx)
                if (response.isSuccessful) {
                    /**
                     * Регистрация успешна
                     * Перенаправляем пользователя на экран ввода OTP-кода
                     * Передаем email и тип "signup" для идентификации процесса регистрации
                     *
                     * Формат маршрута: "verifyOTP/user@example.com/signup"
                     */
                    navController.navigate("verifyOTP/$email/signup")
                } else {
                    // Обработка ошибки сервера (код 4xx, 5xx)
                    // Например: email уже занят, невалидные данные и т.д.
                    errorMessage.value = "Ошибка: ${response.code()}"
                }

            } catch (e: Exception) {
                // Обработка ошибок сети или других исключений
                errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                // В любом случае (успех или ошибка) снимаем состояние загрузки
                isLoading.value = false
            }
        }
    }
}