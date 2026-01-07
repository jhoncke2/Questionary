package com.example.questionary

import com.example.questionary.logic.AudioPlayer
import com.example.questionary.ui.GameStatus

class FakeAudioPlayer : AudioPlayer {

    override fun playAudio(
        status: GameStatus,
        answerIsCorrect: Boolean,
        wonGame: Boolean
    ) {
        // No hace nada
    }

    override fun stopAudio() {
        // No hace nada
    }
}