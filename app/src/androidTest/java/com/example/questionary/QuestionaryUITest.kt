package com.example.questionary

import android.app.Application
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.platform.app.InstrumentationRegistry
import com.example.questionary.ui.AnimationPolicy
import com.example.questionary.ui.LocalAnimationPolicy
import com.example.questionary.ui.QuestionaryScreen
import com.example.questionary.ui.QuestionaryViewModel
import com.example.questionary.ui.compose.QuestionaryTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionaryUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: QuestionaryViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        viewModel = QuestionaryViewModel(
            audioPlayer = FakeAudioPlayer(),
            nQuestionsPerGame = 5,
            repository = FakeQuestionaryRepository(),
            dispatcher = testDispatcher
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun endGameSuccesfully(){
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("start_game")
            .performClick()
        composeTestRule
            .onRoot(useUnmergedTree = true)
            .printToLog("AFTER_CLICK")
        composeTestRule.waitForIdle()
        testDispatcher.scheduler.advanceTimeBy(
            QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION
        )
        composeTestRule.waitForIdle()
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.onRoot(useUnmergedTree = true)
            .printToLog("AFTER_DELAY")
        composeTestRule.runOnIdle {
            Log.d("TEST_STATUS", "GAME STATUS = ${viewModel.uiState.value.status}")
        }
        composeTestRule
            .onNodeWithTag("question_answer_true", useUnmergedTree = true)
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithTag(
            "question_answer_true",
            useUnmergedTree = true
        ).performClick()
    }
}