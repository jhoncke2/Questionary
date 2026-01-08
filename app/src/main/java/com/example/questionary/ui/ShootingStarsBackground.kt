package com.example.questionary.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun ShootingStarsBackground(
    starCount: Int = 5,
    modifier: Modifier = Modifier
) {
    val policy = LocalAnimationPolicy.current
    if (!policy.enableInfiniteAnimations) return

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(starCount) {
            key(it) { // clave para que cada una sea distinta
                ShootingStar(
                    delayMillis = Random.nextLong(0, 3000),
                    starRadius = Random.nextFloat() * 8 + 4,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun ShootingStar(
    delayMillis: Long = 0,
    starRadius: Float = 10f,
    modifier: Modifier = Modifier
) {
    val screenWidth = remember { mutableFloatStateOf(0f) }
    val screenHeight = remember { mutableFloatStateOf(0f) }

    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val baseSlope = 1.5
        while(true){
            delay(delayMillis)

            // El grado de ruido (incertidumbre) en la pendiente de la línea
            val noise = (Random.nextFloat() * 2f - 1f) * 0.15f
            val m = baseSlope + noise
            // Normalización
            val norm = sqrt(1 + m*m)
            // Longitud controlada (1500 -> 2000)
            val longitude = 1525f + Random.nextFloat() * 500f
            val dx = longitude / norm
            val dy = longitude * m / norm
            // 65% prob. de que x sea 0 (necesitamos más puntos en x = 0 que en y = 0)
            val xZeroProb = Random.nextFloat()
            val initX = if(xZeroProb < 0.65) 0f
                else xZeroProb * 1100f * Random.nextFloat()
            val initY = if(initX == 0f) Random.nextFloat() * 2000f
                else 0f
            val endX = (initX + dx).toFloat()
            val endY = (initY + dy).toFloat()


            //val endX = Random.nextFloat() * 1100f + 600f
            //val endY = endX * 1.75f//+ Random.nextFloat()*650f // diagonal
            //val initY = Random.nextFloat() * endY
            //val initX = if(initY > endY*0.3) 0f
            //    else Random.nextFloat() * endX
            xOffset.snapTo(initX)
            yOffset.snapTo(initY)
            alpha.snapTo(1f)

            launch {
                xOffset.animateTo(
                    targetValue = endX,
                    animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
                )
            }
            launch {
                yOffset.animateTo(
                    targetValue = initY + endY,
                    animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 1500)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha.value)
        ) {
            screenWidth.floatValue = size.width
            screenHeight.floatValue = size.height

            drawCircle(
                color = Color.White,
                radius = starRadius,
                center = Offset(xOffset.value, yOffset.value)
            )
        }
    }
}