package com.example.questionary.logic
import android.app.Application
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import com.example.questionary.ui.GameStatus

class AudioPlayer(
    val application: Application
) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    fun playAudio(
        status: GameStatus,
        answerIsCorrect: Boolean = false,
        wonGame: Boolean = false
    ){
        val context = application.applicationContext
        lateinit var audioPath: String
        if(status == GameStatus.WithoutInitialization){
            audioPath = "app_intro.mp3"
            mediaPlayer.setOnCompletionListener {
                it.seekTo(0)
                it.start()
            }
        }else{
            mediaPlayer.setOnCompletionListener(null)
            if(status == GameStatus.AnswerVerified){
                if(answerIsCorrect){
                    audioPath = "success_answer.mp3"
                }else{
                    audioPath = "failure_answer.mp3"
                }
            }else if(status == GameStatus.WaitingForAnswerVerification){
                audioPath = "answer_verification.mp3"
            }else if(status == GameStatus.GameOver){
                if(wonGame){
                    audioPath = "success_game.mp3"
                }else{
                    audioPath = "failure_game.mp3"
                }
            }
        }
        mediaPlayer.apply {
            reset()
            val afd: AssetFileDescriptor = context.assets.openFd(audioPath)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
        }
    }

    fun stopAudio() {
        mediaPlayer.apply {
            //setOnCompletionListener(null)
            stop()
        }
    }
}