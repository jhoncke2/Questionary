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
import kotlin.random.Random

@Composable
fun ShootingStarsBackground(
    starCount: Int = 5,
    modifier: Modifier = Modifier
) {
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
        while(true){
            delay(delayMillis)
            val endX = Random.nextFloat() * 1000f + 500f
            val endY = endX * 1.75f//+ Random.nextFloat()*650f // diagonal
            val initY = Random.nextFloat() * endY
            val initX = if(initY > endY*0.1) 0f
                else Random.nextFloat() * endX
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