package com.example.questionary.ui

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.questionary.R
import com.example.questionary.data.Answer
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun QuestionaryScreen(
    modifier: Modifier = Modifier,
    viewModel: QuestionaryViewModel = viewModel()
){
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val gameUIState by viewModel.uiState.collectAsState()
    val gameStatus = gameUIState.status
    Column (
        modifier = modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(mediumPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(gameStatus == GameStatus.AnswerVerified ||
            gameStatus == GameStatus.Answering ||
            gameStatus == GameStatus.WaitingForAnswerVerification)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ){
                Text(
                    stringResource(R.string.score, gameUIState.score),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                )
                AppIcon(50.dp)
            }
        if(gameStatus == GameStatus.WithoutInitialization)
            GameIntro(
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
            )
        if(gameStatus != GameStatus.GameOver &&
            gameStatus != GameStatus.WithoutInitialization &&
            gameStatus != GameStatus.Loading)
            QuestionCard(
                gameUIState = gameUIState,
                gameStatus = gameStatus,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
            )
        if(gameStatus == GameStatus.GameOver)
            GameOverPanel(
                gameUIState = gameUIState,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
            )
    }
}

@Composable
fun AppIcon(
    height: Dp
){
    val img: Painter = painterResource(R.drawable.questionary_icon)
    Image(
        img,
        alignment = Alignment.Center,
        contentScale = ContentScale.FillHeight,
        contentDescription = null,
        modifier = Modifier
            .height(height)
    )
}

@Composable
fun GameIntro(
    viewModel: QuestionaryViewModel,
    modifier: Modifier = Modifier
){
    val img: Painter = painterResource(R.drawable.questionary_icon)
    Box(
        modifier = modifier.fillMaxSize()
    ){
        ShootingStarsBackground(9, modifier)
        Column (
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(){}
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                AppIcon(250.dp)
                Text(
                    "Questionary",
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {
                    viewModel.resetGame()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_game"),

            ){
                Text(
                    "Empezar a jugar"
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    gameUIState: QuestionaryUIState,
    gameStatus: GameStatus,
    viewModel: QuestionaryViewModel,
    modifier: Modifier = Modifier
){
    val answers = gameUIState.currentQuestion!!.answers
    val currentUserAnswer = gameUIState.currentUserAnswer
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
        )
        Column() {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = dimensionResource(R.dimen.padding_medium)
                    )
                    //.wrapContentHeight()
                    //.height(150.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium
                    ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ){
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        gameUIState.currentQuestion.question,
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .padding(
                                horizontal = 10.dp,
                                vertical = 10.dp
                            )
                            .fillMaxWidth()
                    )
                }
            }
            Column {
                for( answer in answers )
                    AnswerCard(
                        answer = answer,
                        currentUserAnswer = currentUserAnswer,
                        gameStatus = gameStatus,
                        viewModel = viewModel,
                        modifier = modifier
                    )
            }
        }
        if(gameStatus == GameStatus.AnswerVerified)
            Button(
                onClick = {
                    viewModel.goToNextQuestion()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    if(gameUIState.isLastQuestion)
                        stringResource(R.string.end_questions)
                    else
                        stringResource(R.string.next_question),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        else
            Spacer(
                modifier = Modifier
            )
    }
}

@Composable
fun AnswerCard(
    answer: Answer,
    currentUserAnswer: Answer?,
    gameStatus: GameStatus,
    viewModel: QuestionaryViewModel,
    modifier: Modifier = Modifier
){
    var color: Color = MaterialTheme.colorScheme.tertiaryContainer
    val tag = "question_answer_${answer.isCorrect}"
    Log.d("TEST_TAG", "Rendering answer with tag = $tag")
    if(
        currentUserAnswer != null &&
        currentUserAnswer.statement == answer.statement
    ){
        val successColor = Color(0xff5cb85c)
        if(gameStatus == GameStatus.AnswerVerified){
            if(answer.isCorrect)
                color = successColor
            else
                color = MaterialTheme.colorScheme.errorContainer
        }else if(gameStatus == GameStatus.WaitingForAnswerVerification){
            val infiniteTransition = rememberInfiniteTransition(label = "infinite")
            val variableColor by infiniteTransition.animateColor(
                initialValue = successColor,
                targetValue = MaterialTheme.colorScheme.errorContainer,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "color"
            )
            color = variableColor
        }

    }
    Card (
        onClick = {
            viewModel.chooseAnswer(answer)
        },
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 10.dp
            )
            .clip(MaterialTheme.shapes.small)
            .testTag(tag)

    ) {
        Text(
            answer.statement,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
fun GameOverPanel(
    gameUIState: QuestionaryUIState,
    viewModel: QuestionaryViewModel,
    modifier: Modifier = Modifier
){
    var message: String = stringResource(R.string.success_game)
    var img: Painter = painterResource(R.drawable.success_icon)
    if(gameUIState.score < QuestionaryViewModel.MIN_SCORE_TO_WIN){
        message = stringResource(R.string.failure_game)
        img = painterResource(R.drawable.failure_icon)
    }
    Box(
        modifier = modifier.fillMaxSize()
    ){
        ShootingStarsBackground(9, modifier)
        Column (
            modifier = modifier
                .padding(50.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(
                modifier = Modifier
            )
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                Image(
                    img,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillHeight,
                    contentDescription = null,
                    modifier = Modifier
                        .height(250.dp)
                )
            }
            Button(
                onClick = {
                    viewModel.resetGame()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    stringResource(R.string.reset_game),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}