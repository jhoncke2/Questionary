package com.example.questionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.questionary.data.QuestionaryRepository
import com.example.questionary.logic.AudioPlayerImpl
import com.example.questionary.ui.QuestionaryScreen
import com.example.questionary.ui.QuestionaryViewModel
import com.example.questionary.ui.compose.QuestionaryTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: QuestionaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioPlayer = AudioPlayerImpl(application = application)
        viewModel = QuestionaryViewModel(
            audioPlayer,
            7,
            repository = QuestionaryRepository(
                db = FirebaseFirestore.getInstance()
            )
        )
        setContent {
            QuestionaryTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ) {
                    QuestionaryScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuestionaryTheme {
        QuestionaryScreen()
    }
}