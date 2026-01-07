package com.example.questionary.logic

import com.example.questionary.ui.GameStatus

interface AudioPlayer {
    fun playAudio(
        status: GameStatus,
        answerIsCorrect: Boolean = false,
        wonGame: Boolean = false
    )

    fun stopAudio()
}