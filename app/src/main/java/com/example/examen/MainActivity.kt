package com.example.examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.examen.data.UserSession
import com.example.examen.ui.theme.ExamenTheme
import com.example.examen.ui.view.*

/**
 * Главная активность приложения
 * Отвечает за настройку навигации между экранами и инициализацию splash screen
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем splash screen, который отображается при запуске приложения
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Включаем Edge-to-Edge режим (контент может отображаться под системными панелями)
        enableEdgeToEdge()

        setContent {
            // Применяем кастомную тему приложения
            ExamenTheme {
                // Создаем навигационный контроллер для управления переходами между экранами
                val navController = rememberNavController()

                // Scaffold предоставляет базовую структуру экрана (можно добавить верхнюю/нижнюю панель)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /**
                     * NavHost - контейнер для всех экранов приложения
                     * Определяет граф навигации и начальный экран
                     */
                    NavHost(
                        navController = navController,
                        startDestination = "onboarding", // Стартовый экран - онбординг
                        modifier = Modifier.padding(innerPadding) // Учитываем отступы от Scaffold
                    ) {
                        // ============= ЭКРАНЫ АВТОРИЗАЦИИ И ОНБОРДИНГА =============

                        /**
                         * Экран онбординга (приветственные слайды)
                         * Показывается при первом запуске приложения
                         */
                        composable("onboarding") { OnboardingScreen(navController) }

                        /**
                         * Экран входа в приложение
                         */
                        composable("login") { LoginScreen(navController = navController) }

                        /**
                         * Экран регистрации нового пользователя
                         */
                        composable("register") { RegisterScreen(navController = navController) }

                        // ============= ОСНОВНЫЕ ЭКРАНЫ ПРИЛОЖЕНИЯ =============

                        /**
                         * Главный экран с лентой товаров и категориями
                         */
                        composable("home") { HomeScreen(navController = navController) }

                        /**
                         * Экран профиля пользователя
                         * Проверяет наличие авторизации (userId и accessToken)
                         * Если пользователь не авторизован, перенаправляет на экран входа
                         */
                        composable("profile") {
                            val userId = UserSession.userId
                            val accessToken = UserSession.accessToken

                            if (userId != null && accessToken != null) {
                                // Пользователь авторизован - показываем профиль
                                ProfileScreen(
                                    navController = navController,
                                    userId = userId,
                                    accessToken = accessToken
                                )
                            } else {
                                // Пользователь не авторизован - отправляем на вход
                                LoginScreen(navController = navController)
                            }
                        }

                        /**
                         * Экран каталога товаров с фильтрацией по категории
                         * Принимает параметр category в маршруте
                         * Пример: "catalog/Outdoor"
                         */
                        composable(
                            route = "catalog/{category}",
                            arguments = listOf(
                                navArgument("category") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val category =
                                backStackEntry.arguments?.getString("category") ?: "Outdoor"
                            CatalogScreen(
                                navController = navController,
                                initialCategoryTitle = category
                            )
                        }

                        /**
                         * Экран каталога без параметра (по умолчанию "Outdoor")
                         */
                        composable("catalog") {
                            CatalogScreen(
                                navController = navController,
                                initialCategoryTitle = "Outdoor"
                            )
                        }

                        /**
                         * Экран избранного (список товаров, добавленных в избранное)
                         */
                        composable("favorite") {
                            FavoriteScreen(navController = navController)
                        }

                        /**
                         * Экран детальной информации о товаре
                         * Принимает параметр productId в маршруте
                         * Пример: "details/123e4567-e89b-12d3-a456-426614174000"
                         */
                        composable(
                            route = "details/{productId}",
                            arguments = listOf(
                                navArgument("productId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            DetailsScreen(
                                navController = navController,
                                productId = productId
                            )
                        }

                        // ============= ЭКРАНЫ ВОССТАНОВЛЕНИЯ ПАРОЛЯ =============

                        /**
                         * Экран запроса email для восстановления пароля
                         */
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController)
                        }

                        /**
                         * Экран подтверждения OTP-кода
                         * Принимает параметры:
                         * - email: email пользователя
                         * - type: тип операции (signup - регистрация, recovery - восстановление)
                         * Пример: "verifyOTP/user@example.com/recovery"
                         */
                        composable(
                            route = "verifyOTP/{email}/{type}",
                            arguments = listOf(
                                navArgument("email") { type = NavType.StringType },
                                navArgument("type") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val type = backStackEntry.arguments?.getString("type") ?: "signup"
                            VerifyOTPScreen(
                                navController = navController,
                                email = email,
                                otpType = type
                            )
                        }

                        /**
                         * Экран установки нового пароля
                         * Принимает параметр email в маршруте
                         * Пример: "new_password/user@example.com"
                         */
                        composable(
                            route = "new_password/{email}",
                            arguments = listOf(
                                navArgument("email") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            NewPasswordScreen(navController = navController, email = email)
                        }
                    }
                }
            }
        }
    }
}