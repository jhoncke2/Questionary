package com.example.questionary.data

data class Question(
    val id: String = "",
    val question: String = "",
    val answers: List<Answer> = emptyList()
)