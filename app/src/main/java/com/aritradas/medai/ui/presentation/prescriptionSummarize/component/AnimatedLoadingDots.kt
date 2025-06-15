package com.aritradas.medai.ui.presentation.prescriptionSummarize.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue

@Composable
fun AnimatedLoadingDots(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    animationDuration: Int = 1000,
    dotColor: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(dotCount) { index ->
            val delay = (animationDuration / dotCount) * index
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration / dotCount,
                        delayMillis = delay
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )

            Text(
                text = "•",
                color = dotColor.copy(alpha = dotAlpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}