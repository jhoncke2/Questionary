package com.example.questionary.data
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

open class QuestionaryRepository(
    val db: FirebaseFirestore
) {

    open suspend fun loadQuestionary(): List<Question> {
        val snapshot = db.collection("Questionary")
            .get()
            .await()
        return snapshot
            .documents
            .mapNotNull<DocumentSnapshot, Question> {
                it.toObject(Question::class.java)!!.copy(id = it.id)
            }
    }
}