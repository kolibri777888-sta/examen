package com.example.examen.ui.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.examen.ui.viewModel.SignUpViewModel

/**
 * Компонент кнопки регистрации с валидацией и обработкой состояния загрузки
 * Выделен в отдельный компонент для переиспользования и лучшей организации кода
 *
 * @param name введенное имя пользователя
 * @param email введенный email пользователя
 * @param password введенный пароль
 * @param isTermsAccepted флаг согласия с условиями обработки данных
 * @param viewModel ViewModel для регистрации (получается через viewModel())
 * @param navController навигационный контроллер для переходов
 */
@Composable
fun RegisterButton(
    name: String,
    email: String,
    password: String,
    isTermsAccepted: Boolean,
    viewModel: SignUpViewModel = viewModel(),
    navController: NavHostController
) {
    // Получаем контекст для отображения Toast и работы с SharedPreferences
    val context = LocalContext.current

    /**
     * Кнопка регистрации
     * Активируется только когда не в состоянии загрузки (enabled = !viewModel.isLoading.value)
     */
    Button(
        onClick = {
            // Проверяем валидность введенных данных перед отправкой
            if (validateInputs(name, email, password, isTermsAccepted)) {

                /**
                 * Сохраняем email пользователя в SharedPreferences
                 * Это нужно для передачи на экран OTP-проверки, чтобы пользователь
                 * не вводил email повторно
                 */
                val prefs = context.getSharedPreferences(
                    "my_app_preferences", // Имя файла SharedPreferences
                    Context.MODE_PRIVATE   // Приватный режим (только для этого приложения)
                )
                prefs.edit().putString("userEmail", email.trim()).apply()

                /**
                 * Вызов метода регистрации в ViewModel
                 * Сервер отправляет код подтверждения на указанный email
                 */
                viewModel.signUp(email.trim(), password.trim(), navController)

                /**
                 * Переход на экран ввода OTP-кода
                 * Здесь предполагается, что маршрут "otp" зарегистрирован в NavHost
                 * Примечание: возможно, стоит передавать email через параметр маршрута,
                 * например "otp/$email", чтобы не использовать SharedPreferences
                 */
                navController.navigate("otp")
            } else {
                // Показываем Toast с сообщением об ошибке валидации
                Toast.makeText(
                    context,
                    "Заполните все поля корректно",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()   // На всю ширину
            .height(54.dp),    // Фиксированная высота
        shape = RoundedCornerShape(18.dp), // Сильно скругленные углы
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF48B2E7),        // Активный цвет (голубой)
            contentColor = Color.White,                // Цвет текста (белый)
            disabledContainerColor = Color(0xFF2B6B8B), // Неактивный цвет (темно-синий)
            disabledContentColor = Color.White         // Цвет текста в неактивном состоянии
        ),
        enabled = !viewModel.isLoading.value // Кнопка неактивна во время загрузки
    ) {
        // Условный рендеринг: индикатор загрузки или текст
        if (viewModel.isLoading.value) {
            // Показываем круговой индикатор во время выполнения запроса
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            // Показываем текст кнопки в обычном состоянии
            Text(
                text = "Зарегистрироваться",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Функция валидации полей формы регистрации
 * Проверяет корректность введенных данных перед отправкой на сервер
 *
 * @param name имя пользователя
 * @param email email пользователя
 * @param password пароль
 * @param termsAccepted флаг согласия с условиями
 * @return true если все данные валидны, false в противном случае
 *
 * TODO: Реализовать логику валидации:
 * - Проверка что имя не пустое
 * - Проверка email на соответствие формату (использовать Patterns.EMAIL_ADDRESS)
 * - Проверка длины пароля (минимум 6 символов)
 * - Проверка что termsAccepted == true
 */
fun validateInputs(
    name: String,
    email: String,
    password: String,
    termsAccepted: Boolean
): Boolean {
    return TODO("Provide the return value")
}