package com.example.questionary.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionary.data.Answer
import com.example.questionary.data.Question
import com.example.questionary.data.QuestionaryRepository
import com.example.questionary.logic.AudioPlayer
import com.example.questionary.logic.AudioPlayerImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionaryViewModel(
    private val audioPlayer: AudioPlayer,
    private val nQuestionsPerGame: Int,
    private val repository: QuestionaryRepository
): ViewModel() {
    @VisibleForTesting
    internal val usedQuestions: MutableSet<Question> = mutableSetOf()
    @VisibleForTesting
    internal val _uiState = MutableStateFlow(QuestionaryUIState())
    val uiState: StateFlow<QuestionaryUIState> =
        _uiState.asStateFlow()

    companion object {
        const val TIME_OF_ANSWER_VERIFICATION: Long = 3000
        const val SCORE_PER_ANSWER = 100
        const val MIN_SCORE_TO_WIN = 500
    }

    init {
        audioPlayer.playAudio(GameStatus.WithoutInitialization)
    }

    fun chooseAnswer(answer: Answer) {
        if(_uiState.value.status != GameStatus.AnswerVerified
            && _uiState.value.status != GameStatus.WaitingForAnswerVerification){
            var newStatus = GameStatus.WaitingForAnswerVerification
            updateState(
                currentUserAnswer = answer,
                status = newStatus
            )
            viewModelScope.launch {
                audioPlayer.playAudio(
                    status = newStatus
                )
                delay(TIME_OF_ANSWER_VERIFICATION)
                var newScore = _uiState.value.score
                val isCorrect = answer.isCorrect
                if(isCorrect){
                    newScore += SCORE_PER_ANSWER
                }
                newStatus = GameStatus.AnswerVerified
                updateState(
                    status = newStatus,
                    currentScore = newScore
                )
                audioPlayer.playAudio(
                    status = newStatus,
                    answerIsCorrect = isCorrect
                )
            }
        }
    }

    private fun reset(){
        viewModelScope.launch {
            _uiState.value = QuestionaryUIState(
                questionary = emptyList(),
                currentQuestion = null,
                status = GameStatus.Loading
            )
            usedQuestions.clear()
            val questionary = repository.loadQuestionary()
            val currentQuestion = nextQuestion(questionary)
            _uiState.value = QuestionaryUIState(
                questionary = questionary,
                currentQuestion = currentQuestion,
                status = GameStatus.Answering
            )
        }

    }

    fun resetGame(){
        audioPlayer.stopAudio()
        reset()
    }

    private fun nextQuestion(questionary: List<Question>): Question{
        var question = questionary.random()
        while(usedQuestions.any { it -> it.id == question.id }){
            question = questionary.random()
        }
        usedQuestions.add(question)
        return question
    }

    fun goToNextQuestion() {
        audioPlayer.stopAudio()
        if(_uiState.value.isLastQuestion){
            val status = GameStatus.GameOver
            val gameIsWon = _uiState.value.score == MIN_SCORE_TO_WIN
            updateState(
                status = status,
                deleteCurrentUserAnswer = true,
                gameIsWon = gameIsWon
            )
            audioPlayer.playAudio(
                status = status,
                wonGame =  _uiState.value.score >= MIN_SCORE_TO_WIN
            )
        }else{
            val questionary = _uiState.value.questionary
            val question = nextQuestion(questionary)
            val isLastQuestion = usedQuestions.size == nQuestionsPerGame
            updateState(
                currentQuestion = question,
                status = GameStatus.Answering,
                isLastQuestion = isLastQuestion,
                deleteCurrentUserAnswer = true
            )
        }

    }

    private fun updateState(
        currentQuestion: Question? = null,
        currentScore: Int? = null,
        status: GameStatus? = null,
        currentUserAnswer: Answer? = null,
        deleteCurrentUserAnswer: Boolean = false,
        isLastQuestion: Boolean? = null,
        gameIsWon: Boolean? = null
    ){
        _uiState.update { lastState ->
            lastState.copy(
                currentQuestion = currentQuestion ?: lastState.currentQuestion,
                score = currentScore ?: lastState.score,
                status = status ?: lastState.status,
                currentUserAnswer =
                    if(deleteCurrentUserAnswer)
                        null
                    else
                        currentUserAnswer ?: lastState.currentUserAnswer,
                isLastQuestion = isLastQuestion ?: lastState.isLastQuestion,
                gameIsWon = gameIsWon ?: lastState.gameIsWon
            )
        }
    }

}