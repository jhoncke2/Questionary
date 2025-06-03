package com.example.questionary.ui
import com.example.questionary.data.QuestionaryRepository
import com.example.questionary.data.QuestionaryTestRepository
import com.example.questionary.logic.AudioPlayer
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import kotlin.math.roundToLong

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionaryViewModelTest {
    private val nQuestionsPerGame = QuestionaryTestRepository.questionary.size
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val repository: QuestionaryRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: QuestionaryViewModel
    private val states = mutableListOf<QuestionaryUIState>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QuestionaryViewModel(
            audioPlayer,
            nQuestionsPerGame,
            repository
        )
        every { audioPlayer.playAudio(any(), any(), any()) } just Runs
        every { audioPlayer.stopAudio() } just Runs
        coEvery { repository.loadQuestionary() } returns QuestionaryTestRepository.questionary
    }

    @Test
    fun ChooseAnswer_correctAnswer_MoreScoreAndTwoStatusChange(): Unit = runTest{
        val firstQuestion = QuestionaryTestRepository.questionary.first()
        viewModel._uiState.value = QuestionaryUIState(
            questionary = QuestionaryTestRepository.questionary,
            currentQuestion = firstQuestion,
            status = GameStatus.Answering,
            score = 0,
            currentUserAnswer = null
        )
        val uiState = viewModel.uiState.value
        assertTrue(uiState.questionary.isNotEmpty())
        assertEquals(GameStatus.Answering, uiState.status)
        assertEquals(0, uiState.score)
        val rightAnswer = uiState.currentQuestion!!.answers.first { it ->
            it.isCorrect
        }
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.chooseAnswer(rightAnswer)
        testScheduler.advanceTimeBy(QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION/2)

        var lastState = states.last()
        assertEquals(rightAnswer, lastState.currentUserAnswer)
        assertEquals(GameStatus.WaitingForAnswerVerification, lastState.status)
        assertEquals(0, lastState.score)
        verify { audioPlayer.playAudio(
            GameStatus.WaitingForAnswerVerification
        ) }

        testScheduler.advanceTimeBy((QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION * 0.6).roundToLong())
        lastState = states.last()
        job.cancel()

        assertEquals(rightAnswer, lastState.currentUserAnswer)
        assertEquals(GameStatus.AnswerVerified, lastState.status)
        assertEquals(QuestionaryViewModel.SCORE_PER_ANSWER, lastState.score)
        verify { audioPlayer.playAudio(
            GameStatus.AnswerVerified,
            answerIsCorrect = true
        ) }
    }

    @Test
    fun ChooseAnswer_IncorrectAnswer_TheSameScoreAndTwoStatusChanges(): Unit = runTest{
        val firstQuestion = QuestionaryTestRepository.questionary.first()
        viewModel._uiState.value = QuestionaryUIState(
            questionary = QuestionaryTestRepository.questionary,
            currentQuestion = firstQuestion,
            status = GameStatus.Answering,
            score = 0,
            currentUserAnswer = null
        )
        val uiState = viewModel.uiState.value
        assertTrue(uiState.questionary.isNotEmpty())
        assertEquals(GameStatus.Answering, uiState.status)
        assertEquals(0, uiState.score)
        val badAnswer = uiState.currentQuestion!!.answers.first { it ->
            !it.isCorrect
        }
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.chooseAnswer(badAnswer)
        testScheduler.advanceTimeBy(QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION/2)

        var lastState = states.last()
        assertEquals(badAnswer, lastState.currentUserAnswer)
        assertEquals(GameStatus.WaitingForAnswerVerification, lastState.status)
        assertEquals(0, lastState.score)
        verify { audioPlayer.playAudio(
            GameStatus.WaitingForAnswerVerification
        ) }

        testScheduler.advanceTimeBy((QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION * 0.6).roundToLong())
        lastState = states.last()
        job.cancel()

        assertEquals(badAnswer, lastState.currentUserAnswer)
        assertEquals(GameStatus.AnswerVerified, lastState.status)
        assertEquals(0, lastState.score)
        verify { audioPlayer.playAudio(
            GameStatus.AnswerVerified,
            answerIsCorrect = false
        ) }
    }

    @Test
    fun ChooseAnswer_NotAllowedBecauseOfAnswerIsVerified_AllTheSameState(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        val question = questionary.last()
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = question,
            status = GameStatus.AnswerVerified,
            score = 0,
            currentUserAnswer = question.answers.last()
        )
        val answer = question.answers.first()
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.chooseAnswer(answer)
        testScheduler.advanceTimeBy(QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION/2)
        var lastState = states.last()
        assertFalse(answer == lastState.currentUserAnswer)
        assertFalse(lastState.status == GameStatus.WaitingForAnswerVerification)
        assertEquals(0, lastState.score)

        testScheduler.advanceTimeBy((QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION * 0.6).roundToLong())
        lastState = states.last()
        job.cancel()

        assertFalse(answer == lastState.currentUserAnswer)
        assertFalse(lastState.status == GameStatus.WaitingForAnswerVerification)
        assertEquals(0, lastState.score)
    }

    @Test
    fun ChooseAnswer_NotAllowedBecauseOfAnswerIsBeingVerified_AllTheSameState(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        val question = questionary.last()
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = question,
            status = GameStatus.WaitingForAnswerVerification,
            score = 0,
            currentUserAnswer = question.answers.last()
        )
        val answer = question.answers.first()
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.chooseAnswer(answer)
        testScheduler.advanceTimeBy(QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION/2)
        var lastState = states.last()
        assertFalse(answer == lastState.currentUserAnswer)
        assertEquals(lastState.status, GameStatus.WaitingForAnswerVerification)
        assertEquals(0, lastState.score)

        testScheduler.advanceTimeBy((QuestionaryViewModel.TIME_OF_ANSWER_VERIFICATION * 0.6).roundToLong())
        lastState = states.last()
        job.cancel()

        assertFalse(answer == lastState.currentUserAnswer)
        assertEquals(lastState.status, GameStatus.WaitingForAnswerVerification)
        assertEquals(0, lastState.score)

    }

    @Test
    fun GoToNextQuestion_NormalQuestion_QuestionChangedAndStatusUpdated(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        val firstQuestion = questionary.first()
        viewModel.usedQuestions.clear()
        viewModel.usedQuestions.add(firstQuestion)
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = firstQuestion,
            status = GameStatus.AnswerVerified,
            score = QuestionaryViewModel.SCORE_PER_ANSWER,
            currentUserAnswer = firstQuestion.answers.first()
        )
        states.add(viewModel.uiState.value)
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.goToNextQuestion()
        testScheduler.advanceTimeBy(100)
        job.cancel()
        val lastState = states.last()
        assertFalse(lastState.isLastQuestion)
        assertEquals(QuestionaryViewModel.SCORE_PER_ANSWER, lastState.score)
        assertEquals(GameStatus.Answering, lastState.status)
        assertNotEquals(firstQuestion, lastState.currentQuestion)
        assertNull(lastState.currentUserAnswer)
        verify { audioPlayer.stopAudio() }
    }

    @Test
    fun GoToNextQuestion_ToLastQuestion_LastQuestionIsTrueAndSelectedTheOnlyOneUnusedQuestion(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        viewModel.usedQuestions.clear()
        viewModel.usedQuestions.addAll(questionary.subList(0, nQuestionsPerGame-1))
        val initCurrentQuestion = questionary[nQuestionsPerGame-2]
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = initCurrentQuestion,
            status = GameStatus.AnswerVerified,
            score = QuestionaryViewModel.SCORE_PER_ANSWER,
            currentUserAnswer = initCurrentQuestion.answers.first()
        )
        states.add(viewModel.uiState.value)
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.goToNextQuestion()
        testScheduler.advanceTimeBy(100)
        job.cancel()
        val lastState = states.last()
        assertTrue(lastState.isLastQuestion)
        assertEquals(QuestionaryViewModel.SCORE_PER_ANSWER, lastState.score)
        assertEquals(GameStatus.Answering, lastState.status)
        assertEquals(questionary.last().id, lastState.currentQuestion!!.id)
        verify { audioPlayer.stopAudio() }
    }

    @Test
    fun GoToNextQuestion_WonGame_GameOverIsTrueAndWonGameIsTrue(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        viewModel.usedQuestions.clear()
        viewModel.usedQuestions.addAll(questionary)
        val initCurrentQuestion = questionary.last()
        val gameScore = QuestionaryViewModel.MIN_SCORE_TO_WIN
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = initCurrentQuestion,
            status = GameStatus.AnswerVerified,
            score = gameScore,
            isLastQuestion = true,
            currentUserAnswer = initCurrentQuestion.answers.first()
        )
        states.add(viewModel.uiState.value)
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.goToNextQuestion()
        testScheduler.advanceTimeBy(100)
        job.cancel()
        val lastState = states.last()
        assertEquals(gameScore, lastState.score)
        assertEquals(GameStatus.GameOver, lastState.status)
        assertEquals(null, lastState.currentUserAnswer)
        assertTrue(lastState.gameIsWon)
    }

    @Test
    fun GoToNextQuestion_LostGame_GameOverIsTrueAndWonGameIsFalse(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        viewModel.usedQuestions.clear()
        viewModel.usedQuestions.addAll(questionary)
        val initCurrentQuestion = questionary.last()
        val gameScore = QuestionaryViewModel.MIN_SCORE_TO_WIN-1
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = initCurrentQuestion,
            status = GameStatus.AnswerVerified,
            score = gameScore,
            isLastQuestion = true,
            currentUserAnswer = initCurrentQuestion.answers.first()
        )
        states.add(viewModel.uiState.value)
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.goToNextQuestion()
        testScheduler.advanceTimeBy(100)
        job.cancel()
        val lastState = states.last()
        assertEquals(gameScore, lastState.score)
        assertEquals(GameStatus.GameOver, lastState.status)
        assertEquals(null, lastState.currentUserAnswer)
        assertFalse(lastState.gameIsWon)
    }

    @Test
    fun ResetGame_AllViewModelIsResetted(): Unit = runTest{
        val questionary = QuestionaryTestRepository.questionary
        viewModel.usedQuestions.clear()
        viewModel.usedQuestions.addAll(questionary)
        val initCurrentQuestion = questionary.last()
        viewModel._uiState.value = QuestionaryUIState(
            questionary = questionary,
            currentQuestion = initCurrentQuestion,
            status = GameStatus.GameOver,
            score = QuestionaryViewModel.MIN_SCORE_TO_WIN,
            isLastQuestion = true,
            currentUserAnswer = null,
            gameIsWon = true
        )
        states.add(viewModel.uiState.value)
        val job = launch(
            testScheduler
        ) {
            viewModel.uiState.collect{ state ->
                states.add(state)
            }
        }
        viewModel.resetGame()
        testScheduler.advanceTimeBy(100)
        job.cancel()
        val lastState = states.last()
        assertEquals(0, lastState.score)
        assertEquals(GameStatus.Answering, lastState.status)
        assertEquals(null, lastState.currentUserAnswer)
        assertTrue(viewModel.usedQuestions.size == 1)
        assertNotNull(viewModel.uiState.value.currentQuestion)
        assertFalse(lastState.gameIsWon)
    }
}