package com.example.questionary.data

import com.google.firebase.firestore.PropertyName

data class Answer(
    val statement: String = "",
    @get:PropertyName("isCorrect")
    val isCorrect: Boolean = false
)
