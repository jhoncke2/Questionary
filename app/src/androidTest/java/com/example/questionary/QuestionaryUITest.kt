package com.example.questionary

import android.app.Application
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.platform.app.InstrumentationRegistry
import com.example.questionary.logic.AudioPlayerImpl
import com.example.questionary.ui.AnimationPolicy
import com.example.questionary.ui.LocalAnimationPolicy
import com.example.questionary.ui.QuestionaryScreen
import com.example.questionary.ui.QuestionaryViewModel
import com.example.questionary.ui.compose.QuestionaryTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionaryUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: QuestionaryViewModel

    @Before
    fun setup() {
        val appContext =
            InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext as Application

        viewModel = QuestionaryViewModel(
            audioPlayer = FakeAudioPlayer(),
            nQuestionsPerGame = 5,
            repository = FakeQuestionaryRepository()
        )

        composeTestRule.setContent {
            QuestionaryTheme {
                CompositionLocalProvider(
                    LocalAnimationPolicy provides AnimationPolicy(
                        enableInfiniteAnimations = false
                    )
                ) {
                    QuestionaryScreen(viewModel = viewModel)
                }
            }
        }
    }

    @Test
    fun endGameSuccesfully(){
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("start_game")
            .performClick()
        composeTestRule.runOnIdle {
            println("GAME STATUS = ${viewModel.uiState.value.status}")
        }
        composeTestRule
            .onRoot(useUnmergedTree = true)
            .printToLog("AFTER_CLICK")
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag("question_answer_true", useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag(
            "question_answer_true",
            useUnmergedTree = true
        ).performClick()
    }
}