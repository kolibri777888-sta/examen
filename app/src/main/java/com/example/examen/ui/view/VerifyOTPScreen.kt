package com.example.examen.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.examen.R
import com.example.examen.ui.theme.ExamenTheme
import com.example.examen.ui.viewModel.VerifyOTPViewModel

/**
 * Экран ввода OTP-кода (одноразового пароля) для подтверждения email
 * Используется как при регистрации, так и при восстановлении пароля
 *
 * @param navController навигационный контроллер для переходов между экранами
 * @param email email пользователя, на который отправлен код
 * @param otpType тип OTP: "signup" для регистрации или "recovery" для восстановления пароля
 * @param viewModel view-model для обработки логики проверки OTP
 */
@Composable
fun VerifyOTPScreen(
    navController: NavHostController,
    email: String,
    otpType: String = "signup", // "signup" или "recovery"
    viewModel: VerifyOTPViewModel = viewModel()
) {
    // Состояние для хранения введенного OTP-кода
    var otpValue by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val otpLength = 8 // Длина OTP-кода (8 символов)

    /**
     * Эффект, который автоматически запускает проверку OTP
     * Как только длина введенного кода достигает otpLength
     */
    LaunchedEffect(otpValue.text) {
        if (otpValue.text.length == otpLength) {
            // Автоматическая отправка на проверку при заполнении всех полей
            viewModel.verifyOTP(email, otpValue.text, otpType, context, navController)
        }
    }

    // Основная поверхность экрана
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        // Колонка с контентом
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp) // Горизонтальные отступы
        ) {
            // Верхний отступ для визуального баланса
            Spacer(modifier = Modifier.height(50.dp))

            // Кнопка "Назад" для возврата к предыдущему экрану
            Box(
                modifier = Modifier
                    .size(32.dp) // Размер 32x32 dp
                    .clip(RoundedCornerShape(8.dp)) // Скругленные углы
                    .background(Color(0xFFF7F7F7)) // Светло-серый фон
                    .clickable { navController.popBackStack() }, // Возврат на предыдущий экран
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Назад",
                    tint = Color(0xFF555555) // Темно-серый цвет
                )
            }

            // Отступ после кнопки
            Spacer(modifier = Modifier.height(40.dp))

            // Заголовок экрана
            Text(
                text = "OTP Проверка",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Отступ после заголовка
            Spacer(modifier = Modifier.height(12.dp))

            /**
             * Подзаголовок с инструкцией для пользователя
             * Пользователь должен проверить email и ввести полученный код
             */
            Text(
                text = "Пожалуйста, Проверьте Свою\nЭлектронную Почту, Чтобы Увидеть Код\nПодтверждения",
                fontSize = 14.sp,
                color = Color(0xFF7D7D7D),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Отступ перед полем ввода
            Spacer(modifier = Modifier.height(40.dp))

            // Метка для поля OTP
            Text(
                text = "OTP Код",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            /**
             * Кастомное поле для ввода OTP-кода
             * Разбито на отдельные ячейки для лучшего UX
             */
            OtpInputField(
                otpValue = otpValue,
                onValueChange = {
                    // Ограничение длины ввода
                    if (it.text.length <= otpLength) {
                        otpValue = it
                    }
                },
                length = otpLength
            )

            // Отступ после поля ввода
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Компонент для ввода OTP-кода с отдельными ячейками
 * Использует BasicTextField для полного контроля над отображением
 *
 * @param otpValue текущее значение OTP-кода
 * @param onValueChange колбэк при изменении значения
 * @param length длина OTP-кода (количество ячеек)
 */
@Composable
fun OtpInputField(
    otpValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    length: Int
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        /**
         * Базовое текстовое поле без визуального оформления
         * Прозрачный текст, используется только для ввода
         */
        BasicTextField(
            value = otpValue,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Цифровая клавиатура
            decorationBox = {
                /**
                 * Декорация поля - отображаем ряд ячеек
                 * Скрываем реальное поле ввода, показываем кастомный UI
                 */
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, // Равномерное распределение
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(length) { index ->
                        // Получаем символ для текущей позиции, если он уже введен
                        val char = if (index < otpValue.text.length) otpValue.text[index] else null
                        // Определяем, находится ли фокус на этой ячейке
                        val isFocused = index == otpValue.text.length

                        OtpCell(char = char, isFocused = isFocused)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent) // Прозрачный текст
        )
    }
}

/**
 * Отдельная ячейка для ввода одного символа OTP-кода
 *
 * @param char символ для отображения (null если ячейка пуста)
 * @param isFocused флаг, указывающий что эта ячейка сейчас в фокусе (текущая позиция ввода)
 */
@Composable
fun OtpCell(
    char: Char?,
    isFocused: Boolean
) {
    // Цвет границы: красный если ячейка в фокусе, иначе серый (но граница не отображается)
    val borderColor = if (isFocused) Color(0xFFFF5252) else Color(0xFFF7F7F7)
    // Цвет фона всегда светло-серый
    val backgroundColor = Color(0xFFF7F7F7)

    Box(
        modifier = Modifier
            .width(42.dp) // Ширина ячейки
            .height(60.dp) // Высота ячейки
            .clip(RoundedCornerShape(12.dp)) // Скругленные углы
            .background(backgroundColor)
            .border(
                width = if (isFocused) 1.dp else 0.dp, // Граница только для фокуса
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Отображение символа (пустая строка если символа нет)
        Text(
            text = char?.toString() ?: "",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Предпросмотр экрана OTP-проверки для разработки
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyOTPScreenPreview() {
    ExamenTheme {
        val navController = rememberNavController()
        VerifyOTPScreen(
            navController = navController,
            email = "test@example.com",
            otpType = "recovery"
        )
    }
}