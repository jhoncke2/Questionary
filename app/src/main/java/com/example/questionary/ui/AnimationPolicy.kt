package com.example.questionary.ui

import androidx.compose.runtime.staticCompositionLocalOf

data class AnimationPolicy(
    val enableInfiniteAnimations: Boolean
)

val LocalAnimationPolicy = staticCompositionLocalOf {
    AnimationPolicy(enableInfiniteAnimations = true)
}
