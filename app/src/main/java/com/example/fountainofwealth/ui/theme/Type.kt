package com.example.fountainofwealth.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 34.sp),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = Typography().bodyLarge.copy(lineHeight = 24.sp)
)
