package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

class SnakeActivity : ComponentActivity() {

    private lateinit var snakeViewModel: SnakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snakeViewModel = ViewModelProvider(this)[SnakeViewModel::class.java]

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
