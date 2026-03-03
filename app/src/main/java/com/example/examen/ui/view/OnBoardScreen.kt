package com.example.examen.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.examen.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Данные для слайдов
    val pages = listOf(
        OnboardPageData(
            imageRes = R.drawable.onboard1,
            title = "ДОБРО\nПОЖАЛОВАТЬ",
            subtitle = "",
            isFirstPage = true
        ),
        OnboardPageData(
            imageRes = R.drawable.onboard2,
            title = "Начнем\nпутешествие",
            subtitle = "Умная, великолепная и модная\nколлекция. Изучите сейчас",
            isFirstPage = false
        ),
        OnboardPageData(
            imageRes = R.drawable.onboard3,
            title = "У Вас Есть Сила,\nЧтобы",
            subtitle = "В вашей комнате много красивых\nи привлекательных растений",
            isFirstPage = false
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF48B2E7),
                        Color(0xFF44A9DC),
                        Color(0xFF2B6B8B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // HorizontalPager для свайпа
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardPage(
                    data = pages[page]
                )
            }

            // Индикаторы и кнопка
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Индикаторы страниц
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    repeat(pagerState.pageCount) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .width(if (isSelected) 24.dp else 8.dp)
                                .height(6.dp)
                                .background(
                                    color = if (isSelected) Color.White else Color(0x55FFFFFF),
                                    shape = RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }

                // Кнопка действия
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < pagerState.pageCount - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                navController.navigate("register")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF48B2E7)
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == pagerState.pageCount - 1) "Завершить" else "Далее",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class OnboardPageData(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val isFirstPage: Boolean
)

@Composable
fun OnboardPage(
    data: OnboardPageData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.isFirstPage) {
            // Специальное смещение для первого слайда
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(1.15f)
                    .height(320.dp)
                    .offset(x = 20.dp, y = (-20).dp)
            )
        } else {
            // Стандартное смещение для остальных слайдов
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(1.25f)
                    .height(340.dp)
                    .offset(y = (-40).dp)
            )
        }

        Spacer(modifier = Modifier.height(if (data.isFirstPage) 24.dp else 16.dp))

        Text(
            text = data.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        if (data.subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = data.subtitle,
                fontSize = 14.sp,
                color = Color(0xFFE0E0E0),
                textAlign = TextAlign.Center
            )
        }
    }
}