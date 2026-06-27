package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PolishBackground

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // Animate positions of aurora blobs to create an active, breathing, organic feel
    val shiftX1 by infiniteTransition.animateFloat(
        initialValue = -60f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX1"
    )
    val shiftY1 by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftY1"
    )

    val shiftX2 by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = -120f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX2"
    )
    val shiftY2 by infiniteTransition.animateFloat(
        initialValue = 60f,
        targetValue = -180f,
        animationSpec = infiniteRepeatable(
            animation = tween(13000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftY2"
    )

    val shiftX3 by infiniteTransition.animateFloat(
        initialValue = -90f,
        targetValue = 90f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX3"
    )
    val shiftY3 by infiniteTransition.animateFloat(
        initialValue = 140f,
        targetValue = -140f,
        animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftY3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PolishBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw the base cosmic dark canvas
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0E1116), // Extra deep dark space
                        Color(0xFF15181E)  // Dark slate-gray background
                    )
                )
            )

            // 2. Glowing Aurora blob 1: Soft Electric Purple / VioletTheme
            val center1 = Offset(width * 0.25f + shiftX1, height * 0.35f + shiftY1)
            val radius1 = width * 0.85f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6).copy(alpha = 0.22f), // VioletTheme
                        Color.Transparent
                    ),
                    center = center1,
                    radius = radius1
                ),
                center = center1,
                radius = radius1
            )

            // 3. Glowing Aurora blob 2: Bright Neon Cyan
            val center2 = Offset(width * 0.75f + shiftX2, height * 0.25f + shiftY2)
            val radius2 = width * 0.75f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF06B6D4).copy(alpha = 0.18f), // Cyan highlight
                        Color.Transparent
                    ),
                    center = center2,
                    radius = radius2
                ),
                center = center2,
                radius = radius2
            )

            // 4. Glowing Aurora blob 3: Magenta / Soft Pink
            val center3 = Offset(width * 0.5f + shiftX3, height * 0.75f + shiftY3)
            val radius3 = width * 0.95f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFD7BDE4).copy(alpha = 0.16f), // PolishHighlightBg (lavender)
                        Color.Transparent
                    ),
                    center = center3,
                    radius = radius3
                ),
                center = center3,
                radius = radius3
            )

            // 5. Glowing Aurora blob 4: Deep Sapphire Blue
            val center4 = Offset(width * 0.1f - shiftX2, height * 0.85f - shiftY1)
            val radius4 = width * 0.7f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3B82F6).copy(alpha = 0.15f), // Royal electric blue
                        Color.Transparent
                    ),
                    center = center4,
                    radius = radius4
                ),
                center = center4,
                radius = radius4
            )
        }

        // Frosted-glass tinting filter overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x2B05070B)) // Ultra sheer overlay to bind colors together
        ) {
            content()
        }
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderAlpha: Float = 0.14f,
    bgAlpha: Float = 0.38f,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderAlpha * 1.5f),
                        Color.White.copy(alpha = borderAlpha * 0.5f),
                        Color.Transparent,
                        Color.White.copy(alpha = borderAlpha * 0.2f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = bgAlpha) // Dynamic rich dark glass base
        ),
        shape = RoundedCornerShape(cornerRadius),
        content = content
    )
}
