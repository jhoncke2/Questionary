package com.example.questionary.ui

import com.example.questionary.data.Answer
import com.example.questionary.data.Question

enum class GameStatus{
    WithoutInitialization,
    Loading,
    Answering,
    WaitingForAnswerVerification,
    AnswerVerified,
    GameOver
}

data class QuestionaryUIState(
    val questionary: List<Question> = listOf(),
    val currentQuestion: Question? = null,
    val score: Int = 0,
    val status: GameStatus = GameStatus.WithoutInitialization,
    val currentUserAnswer: Answer? = null,
    val isLastQuestion: Boolean = false,
    val gameIsWon: Boolean = false
)
