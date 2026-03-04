package com.example.examen.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.examen.R
import com.example.examen.ui.viewModel.NewPasswordViewModel

/**
 * Экран установки нового пароля после подтверждения OTP-кода
 * Позволяет пользователю ввести и подтвердить новый пароль
 *
 * @param navController навигационный контроллер для переходов между экранами
 * @param email email пользователя, переданный с OTP-экрана (ВАЖНО: используется для идентификации)
 * @param viewModel view-model для обработки логики смены пароля (получается через viewModel())
 */
@Composable
fun NewPasswordScreen(
    navController: NavHostController,
    email: String, // ВАЖНО: сюда передаём email из OTP‑экрана
    viewModel: NewPasswordViewModel = viewModel()
) {
    // Состояния для хранения введенных паролей
    var password by remember { mutableStateOf("") } // Основной пароль
    var confirmPassword by remember { mutableStateOf("") } // Подтверждение пароля

    /**
     * Валидация введенных данных:
     * - длина пароля не менее 6 символов
     * - пароль и подтверждение совпадают
     * Результат используется для активации/деактивации кнопки "Сохранить"
     */
    val isValid = password.length >= 6 && password == confirmPassword

    // Основная поверхность экрана на весь размер
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        // Вертикальная колонка с отступами по краям
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Верхний отступ для визуального баланса
            Spacer(modifier = Modifier.height(50.dp))

            // Кнопка "Назад" для возврата к предыдущему экрану (OTP)
            Box(
                modifier = Modifier
                    .size(32.dp) // Размер кнопки 32x32 dp
                    .clip(RoundedCornerShape(8.dp)) // Скругление углов
                    .background(Color(0xFFF7F7F7)) // Светло-серый фон
                    .clickable { navController.popBackStack() }, // Возврат на предыдущий экран
                contentAlignment = Alignment.Center
            ) {
                // Иконка стрелки назад
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null, // Декоративный элемент, описание не требуется
                    tint = Color.Black
                )
            }

            // Отступ после кнопки
            Spacer(modifier = Modifier.height(30.dp))

            // Заголовок экрана
            Text(
                "Задать Новый Пароль",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // Выравнивание по центру
                modifier = Modifier.fillMaxWidth() // На всю ширину
            )

            // Подзаголовок с пояснением
            Text(
                "Установите Новый Пароль Для Входа В\nВашу Учетную Запись",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Отступ перед полями ввода
            Spacer(modifier = Modifier.height(40.dp))

            // Метка для поля "Пароль"
            Text("Пароль", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            /**
             * Поле ввода нового пароля
             * Использует PasswordVisualTransformation для скрытия символов
             */
            OutlinedTextField(
                value = password,
                onValueChange = { password = it }, // Обновление состояния при вводе
                visualTransformation = PasswordVisualTransformation(), // Скрытие ввода (точки вместо символов)
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), // Скругленные углы
                placeholder = { Text("********") }, // Текст-подсказка
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF7F7F7), // Цвет фона когда поле не в фокусе
                    focusedContainerColor = Color(0xFFF7F7F7),   // Цвет фона когда поле в фокусе
                    unfocusedBorderColor = Color.Transparent,    // Убираем рамку когда не в фокусе
                    focusedBorderColor = Color.Transparent       // Убираем рамку когда в фокусе
                )
            )

            // Отступ между полями
            Spacer(modifier = Modifier.height(16.dp))

            // Метка для поля "Подтверждение пароля"
            Text("Подтверждение пароля", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            /**
             * Поле для подтверждения пароля
             * Должно совпадать с первым полем для успешной валидации
             */
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("********") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF7F7F7),
                    focusedContainerColor = Color(0xFFF7F7F7),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Отступ перед кнопкой
            Spacer(modifier = Modifier.height(30.dp))

            /**
             * Кнопка сохранения нового пароля
             * Активируется только при выполнении условий валидации и отсутствии загрузки
             */
            Button(
                onClick = {
                    // Вызов метода viewModel для смены пароля
                    // Передаем очищенный от пробелов email и новый пароль
                    viewModel.changePassword(email.trim(), password.trim(), navController)
                },
                enabled = isValid && !viewModel.isLoading.value, // Кнопка активна только при валидных данных и не в процессе загрузки
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF48B2E7),        // Активный цвет кнопки (голубой)
                    disabledContainerColor = Color(0xFF2B6B8B), // Неактивный цвет кнопки (темно-синий)
                    contentColor = Color.White                  // Цвет текста (белый)
                )
            ) {
                // Условный показ: индикатор загрузки или текст
                if (viewModel.isLoading.value) {
                    // Показываем круговой индикатор во время выполнения запроса
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    // Показываем текст "Сохранить" в обычном состоянии
                    Text("Сохранить")
                }
            }
        }
    }
}