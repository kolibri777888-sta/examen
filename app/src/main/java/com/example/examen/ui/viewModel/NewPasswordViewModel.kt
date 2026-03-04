package com.example.examen.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.examen.data.RetrofitInstance
import com.example.examen.data.model.ChangePasswordRequest
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана установки нового пароля (NewPasswordScreen)
 * Отвечает за отправку нового пароля на сервер и управление навигацией после успешной смены
 */
class NewPasswordViewModel : ViewModel() {

    /**
     * Индикатор загрузки для отображения прогресса во время выполнения запроса
     * true - выполняется запрос (показываем индикатор)
     * false - запрос не выполняется
     */
    val isLoading = mutableStateOf(false)

    /**
     * Сообщение об ошибке при смене пароля
     * null - ошибки нет
     * не null - текст ошибки для отображения пользователю
     */
    val errorMessage = mutableStateOf<String?>(null)

    /**
     * Отправляет запрос на смену пароля
     * При успешном изменении перенаправляет пользователя на экран входа
     * При ошибке сохраняет сообщение об ошибке
     *
     * @param email email пользователя, для которого меняется пароль
     * @param newPassword новый пароль пользователя
     * @param navController навигационный контроллер для перехода на экран входа
     */
    fun changePassword(email: String, newPassword: String, navController: NavController) {
        // Запускаем корутину в скоупе ViewModel
        // Это гарантирует, что запрос будет отменен при уничтожении ViewModel
        viewModelScope.launch {
            try {
                // Устанавливаем состояние загрузки и сбрасываем предыдущую ошибку
                isLoading.value = true
                errorMessage.value = null

                // Создаем тело запроса с email и новым паролем
                val body = ChangePasswordRequest(email, newPassword)

                /**
                 * Отправка POST-запроса на сервер для смены пароля
                 * Тело запроса: {"email": "user@example.com", "new_password": "newPass123"}
                 */
                val response = RetrofitInstance.userManagementService.changePassword(body)

                // Проверяем успешность ответа (код 2xx)
                if (response.isSuccessful) {
                    // Пароль успешно изменен
                    // Перенаправляем пользователя на экран входа
                    navController.navigate("login") {
                        /**
                         * Очищаем стек навигации:
                         * popUpTo("login") inclusive = true удаляет все предыдущие экраны
                         * из стека, включая сам экран login, а затем создает новый экземпляр
                         * Это предотвращает возможность вернуться назад к экрану смены пароля
                         */
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // Обработка ошибки сервера (код 4xx, 5xx)
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