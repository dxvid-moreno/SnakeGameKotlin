package com.example.snake.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snake.viewmodel.ScoreViewModel

class ScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ScoreContent(onBack = { finish() })
            }
        }
    }
}

@Composable
fun ScoreContent(onBack: () -> Unit) {
    val viewModel: ScoreViewModel = viewModel()
    val scoreList by viewModel.scoreList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadScores()
    }
    ScoreScreen(scoreList = scoreList, onBack = onBack)
}
