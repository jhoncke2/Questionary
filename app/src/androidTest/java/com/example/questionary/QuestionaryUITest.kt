package com.example.questionary

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.questionary.data.QuestionaryRepository
import com.example.questionary.logic.AudioPlayer
import com.example.questionary.ui.QuestionaryScreen
import com.example.questionary.ui.QuestionaryViewModel
import com.example.questionary.ui.compose.QuestionaryTheme
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Rule
import org.junit.Test

class QuestionaryUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun endGameSuccesfully(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        composeTestRule.setContent {
            val audioPlayer = AudioPlayer(appContext)
            LocalContext
            QuestionaryTheme {
                QuestionaryScreen(
                    viewModel = QuestionaryViewModel(
                        audioPlayer,
                        5,
                        QuestionaryRepository(
                            FirebaseFirestore.getInstance()
                        )
                    )
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("question_answer_true")
            .performClick()
    }
}