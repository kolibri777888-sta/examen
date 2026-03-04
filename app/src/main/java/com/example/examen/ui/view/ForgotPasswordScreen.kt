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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examen.R
import com.example.examen.ui.viewModel.ForgotPasswordViewModel

/**
 * Экран восстановления пароля (Забыл пароль)
 * Позволяет пользователю ввести email для получения кода восстановления
 *
 * @param navController навигационный контроллер для переходов между экранами
 * @param viewModel view-model для обработки логики отправки email
 */
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    // Состояние для хранения введенного email
    var email by remember { mutableStateOf("") }
    // Состояние диалога подтверждения (получаем из ViewModel)
    val showDialog = viewModel.showDialog.value // Диалог "Проверьте ваш Email"

    /**
     * Диалог успешной отправки письма для восстановления пароля
     * Появляется после успешной отправки кода на email
     */
    if (showDialog) {
        AlertDialog(
            // Закрытие диалога при клике вне его области
            onDismissRequest = { viewModel.showDialog.value = false },
            containerColor = Color.White, // Белый фон диалога

            // Иконка в шапке диалога (конверт)
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp) // Размер иконки
                        .clip(RoundedCornerShape(24.dp)) // Скругление для круглой формы
                        .background(Color(0xFF48B2E7)), // Голубой фон
                    contentAlignment = Alignment.Center
                ) {
                    // Иконка email (конверт)
                    Icon(
                        painter = painterResource(id = R.drawable.email_icon),
                        contentDescription = null, // Декоративный элемент
                        tint = Color.White // Белая иконка на голубом фоне
                    )
                }
            },

            // Заголовок диалога
            title = {
                Text(
                    "Проверьте Ваш Email",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },

            // Текст сообщения в диалоге
            text = {
                Text(
                    "Мы Отправили Код Восстановления Пароля На Вашу Электронную Почту.",
                    textAlign = TextAlign.Center, // Выравнивание по центру
                    color = Color.Gray // Серый цвет текста
                )
            },

            // Кнопка подтверждения в диалоге
            confirmButton = {
                // При нажатии переходим на OTP экран для ввода кода
                Button(
                    onClick = {
                        // Закрываем диалог
                        viewModel.showDialog.value = false
                        // Навигация на экран ввода OTP-кода
                        // Передаем email и тип "recovery", чтобы OTP экран знал, что мы восстанавливаем пароль
                        navController.navigate("verifyOTP/$email/recovery")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48B2E7))
                ) {
                    Text("ОК") // Текст на кнопке
                }
            }
        )
    }

    // Основная поверхность экрана
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        // Вертикальная колонка с отступами
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp) // Внутренние отступы со всех сторон
        ) {
            // Верхний отступ для визуального баланса
            Spacer(modifier = Modifier.height(50.dp))

            // Кнопка "Назад" для возврата к предыдущему экрану
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
                    contentDescription = null, // Декоративный элемент
                    tint = Color.Black // Черная иконка
                )
            }

            // Отступ после кнопки
            Spacer(modifier = Modifier.height(30.dp))

            // Заголовок экрана
            Text(
                "Забыл Пароль",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(), // На всю ширину
                textAlign = TextAlign.Center // Выравнивание по центру
            )

            // Отступ между заголовком и подзаголовком
            Spacer(modifier = Modifier.height(8.dp))

            // Подзаголовок с пояснением
            Text(
                "Введите Свою Учетную Запись\nДля Сброса", // \n для переноса строки
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Отступ перед полем ввода
            Spacer(modifier = Modifier.height(40.dp))

            /**
             * Поле ввода email адреса
             * Material Design 3 компонент с кастомными настройками
             */
            OutlinedTextField(
                value = email, // Текущее значение
                onValueChange = { email = it }, // Обновление состояния при вводе
                placeholder = { Text("xyz@gmail.com") }, // Текст-подсказка
                modifier = Modifier.fillMaxWidth(), // На всю ширину
                shape = RoundedCornerShape(12.dp), // Скругленные углы
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF7F7F7), // Цвет фона когда поле не в фокусе
                    focusedContainerColor = Color(0xFFF7F7F7),   // Цвет фона когда поле в фокусе
                    unfocusedBorderColor = Color.Transparent,    // Убираем рамку когда не в фокусе
                    focusedBorderColor = Color.Transparent       // Убираем рамку когда в фокусе
                )
            )

            // Отступ перед кнопкой
            Spacer(modifier = Modifier.height(30.dp))

            /**
             * Кнопка отправки email для восстановления пароля
             */
            Button(
                onClick = {
                    // Вызов метода ViewModel для отправки письма с кодом восстановления
                    viewModel.sendRecoveryEmail(email)
                },
                modifier = Modifier
                    .fillMaxWidth() // На всю ширину
                    .height(50.dp), // Фиксированная высота
                shape = RoundedCornerShape(12.dp), // Скругленные углы
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF48B2E7) // Голубой цвет кнопки
                )
            ) {
                Text("Отправить") // Текст на кнопке
            }
        }
    }
}