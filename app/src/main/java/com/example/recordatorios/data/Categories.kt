package com.example.recordatorios.data

import androidx.compose.ui.graphics.Color
import com.example.recordatorios.domain.Category

val categories = setOf(
    Category(1L, "Leer"),
    Category(2L, "Hacer"),
    Category(3L, "Mirar"),
)

val categoriesColorMapping = mapOf(
    1L to Color(0xFFBBDEFB),
    2L to Color(0xFFC8E6C9),
    3L to Color(0xFFFFF9C4),
)
