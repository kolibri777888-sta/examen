package com.example.examen.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.examen.R
import com.example.examen.data.RetrofitInstance
import com.example.examen.data.service.ProfileDto
import kotlinx.coroutines.launch
import java.io.File

/**
 * Экран профиля пользователя
 * Отображает информацию о пользователе, позволяет редактировать данные и загружать фото
 *
 * @param navController навигационный контроллер для переходов между экранами
 * @param userId уникальный идентификатор пользователя из Supabase auth.users
 * @param accessToken токен доступа для авторизации запросов к API
 */
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userId: String,          // uuid из Supabase auth.users
    accessToken: String      // access_token из signIn/signUp
) {
    // Контекст для доступа к системным сервисам и ресурсам
    val context = LocalContext.current
    // Корутин скоуп для асинхронных операций
    val scope = rememberCoroutineScope()

    // Состояние режима редактирования
    var isEditing by remember { mutableStateOf(false) }

    // Состояния для полей профиля
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    // Состояния загрузки и ошибок
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // ---------- Камера и разрешения ----------
    /**
     * Создание временного URI для сохранения фото с камеры
     * Используется FileProvider для безопасного доступа к файлам
     */
    val tmpImageUri = remember {
        val file = File(context.cacheDir, "profile_photo.jpg") // Файл в кэше приложения
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /**
     * Лаунчер для камеры - запускает приложение камеры и получает результат
     */
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) avatarUri = tmpImageUri // Если фото сделано успешно, сохраняем URI
    }

    /**
     * Лаунчер для запроса разрешения на использование камеры
     */
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(tmpImageUri) // Если разрешение получено, запускаем камеру
        else Toast.makeText(context, "Нужен доступ к камере", Toast.LENGTH_SHORT).show()
    }

    /**
     * Функция запуска камеры с проверкой разрешений
     */
    fun launchCamera() {
        val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        if (ok) cameraLauncher.launch(tmpImageUri) else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // ---------- Загрузка профиля ----------
    /**
     * Загрузка данных профиля при первом входе на экран
     * Выполняется при изменении userId или accessToken
     */
    LaunchedEffect(userId, accessToken) {
        isLoading = true
        try {
            val service = RetrofitInstance.userManagementService
            // Запрос к API для получения профиля пользователя
            val list: List<ProfileDto> = service.getProfile(
                authHeader = "Bearer $accessToken",
                userIdFilter = "eq.${userId}" // Фильтр по ID пользователя
            )
            val profile = list.firstOrNull()
            if (profile != null) {
                // Заполняем поля данными из профиля
                firstName = profile.firstname.orEmpty()
                lastName = profile.lastname.orEmpty()
                address = profile.address.orEmpty()
                phone = profile.phone.orEmpty()
            } else {
                errorText = "Профиль не найден"
            }
        } catch (e: Exception) {
            errorText = "Не удалось загрузить профиль: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    // Scaffold - базовая структура экрана с нижней навигацией
    Scaffold(
        containerColor = Color.White,
        bottomBar = { BottomBar(navController = navController, currentRoute = "profile") }
    ) { innerPadding ->
        // Основной контейнер с учетом отступов от Scaffold
        Box(modifier = Modifier.padding(innerPadding)) {
            // Колонка с прокруткой для всего контента
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Вертикальная прокрутка
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Верхний отступ
                Spacer(modifier = Modifier.height(16.dp))

                // Верхняя панель с заголовком и кнопкой редактирования
                TopHeader(isEditing = isEditing, onEditClick = { isEditing = !isEditing })

                Spacer(modifier = Modifier.height(24.dp))

                // Секция аватара (фото профиля)
                AvatarSection(
                    avatarUri = avatarUri,
                    onClick = { if (isEditing) launchCamera() } // Камера доступна только в режиме редактирования
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Имя и фамилия пользователя
                Text(
                    text = "$firstName $lastName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Карточка со штрих-кодом
                BarcodeCard()

                Spacer(modifier = Modifier.height(24.dp))

                // Поля профиля (динамические - редактируемые или только для чтения)
                ProfileField("Имя", firstName, { firstName = it }, isEditing)
                ProfileField("Фамилия", lastName, { lastName = it }, isEditing)
                ProfileField("Адрес", address, { address = it }, isEditing)
                ProfileField("Телефон", phone, { phone = it }, isEditing)

                // Кнопка сохранения (отображается только в режиме редактирования)
                if (isEditing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Сохранение изменений профиля
                            scope.launch {
                                isLoading = true
                                try {
                                    // Подготовка данных для отправки
                                    val body = mapOf(
                                        "firstname" to firstName,
                                        "lastname" to lastName,
                                        "address" to address,
                                        "phone" to phone
                                    )
                                    // Отправка запроса на обновление профиля
                                    val resp = RetrofitInstance.userManagementService.updateProfile(
                                        authHeader = "Bearer $accessToken",
                                        userIdFilter = "eq.$userId",
                                        body = body
                                    )
                                    if (resp.isSuccessful) {
                                        isEditing = false // Выход из режима редактирования при успехе
                                    } else {
                                        errorText = "Ошибка сохранения: ${resp.code()}"
                                    }
                                } catch (e: Exception) {
                                    errorText = "Не удалось сохранить профиль: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48B2E7))
                    ) {
                        Text("Сохранить", fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Нижний отступ для учета нижней навигации
                Spacer(modifier = Modifier.height(80.dp))
            }

            // Оверлей загрузки (полупрозрачный фон с индикатором)
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f)), // Полупрозрачный черный фон
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF48B2E7))
                }
            }
        }
    }

    // Диалог ошибки (появляется при наличии errorText)
    if (errorText != null) {
        AlertDialog(
            onDismissRequest = { errorText = null },
            title = { Text("Ошибка") },
            text = { Text(errorText ?: "") },
            confirmButton = {
                TextButton(onClick = { errorText = null }) {
                    Text("OK")
                }
            }
        )
    }
}

// ---------- Вспомогательные composable-функции ----------

/**
 * Верхняя панель с заголовком и кнопкой редактирования/готово
 * @param isEditing флаг режима редактирования
 * @param onEditClick колбэк при клике на кнопку
 */
@Composable
fun TopHeader(isEditing: Boolean, onEditClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Заголовок по центру
        Text(
            text = "Профиль",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.align(Alignment.Center)
        )

        // Кнопка справа
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isEditing) Color.Transparent else Color(0xFF48B2E7)) // В режиме редактирования прозрачный фон
                .clickable { onEditClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isEditing) {
                // В режиме редактирования показываем текст "Готово"
                Text("Готово", fontSize = 12.sp, color = Color(0xFF48B2E7), fontWeight = FontWeight.Bold)
            } else {
                // В обычном режиме показываем иконку редактирования
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Секция аватара пользователя
 * @param avatarUri URI выбранного изображения (может быть null)
 * @param onClick колбэк при клике на аватар
 */
@Composable
fun AvatarSection(avatarUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape) // Круглая форма
            .background(Color.LightGray) // Серый фон-заглушка
            .clickable { onClick() }, // Кликабельно для выбора фото
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri != null) {
            // Если есть выбранное фото, отображаем его
            Image(
                painter = rememberAsyncImagePainter(model = avatarUri), // Coil для загрузки изображения
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop, // Обрезка для заполнения круга
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Если фото нет, показываем иконку-заглушку
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Карточка со штрих-кодом (стилизованный элемент интерфейса)
 */
@Composable
fun BarcodeCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левая часть с вертикальным текстом "Открыть"
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Открыть",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.rotate(-90f), // Поворот текста на -90 градусов
                maxLines = 1
            )
        }

        // Правая часть с изображением штрих-кода
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_barcode),
                contentDescription = "Barcode",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillBounds // Растягивание для заполнения
            )
        }
    }
}

/**
 * Поле профиля с заголовком и редактируемым текстом
 * @param title заголовок поля
 * @param value текущее значение
 * @param onValueChange колбэк при изменении значения
 * @param isEditing флаг режима редактирования (доступно только при true)
 */
@Composable
fun ProfileField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Заголовок поля
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF888888),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Базовое текстовое поле (без Material оформления)
        BasicTextField(
            value = value,
            onValueChange = { if (isEditing) onValueChange(it) }, // Изменение только в режиме редактирования
            enabled = isEditing, // Поле активно только в режиме редактирования
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
                fontSize = 16.sp
            ),
            // Декорация поля (фон, отступы)
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F7F7)) // Светло-серый фон
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField() // Содержимое поля
                }
            }
        )
    }
}