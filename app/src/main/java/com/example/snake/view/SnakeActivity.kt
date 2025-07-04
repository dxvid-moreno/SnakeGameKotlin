package com.example.snake.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.snake.viewmodel.SnakeViewModel

class SnakeActivity : ComponentActivity() {

    private lateinit var snakeViewModel: SnakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snakeViewModel = ViewModelProvider(this)[SnakeViewModel::class.java]
        val difficultyName = intent.getStringExtra("difficulty") ?: "MEDIUM"
        snakeViewModel.difficulty.value = SnakeViewModel.Difficulty.valueOf(difficultyName)

        setContent {
            SnakeGame(viewModel = snakeViewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::snakeViewModel.isInitialized) {
            snakeViewModel.pauseGame()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (::snakeViewModel.isInitialized) {
            snakeViewModel.resumeGame()
        }
    }
}

