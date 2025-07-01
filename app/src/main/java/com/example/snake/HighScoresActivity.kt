package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.Score

class HighScoresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HighScoresScreen()
        }
    }
}

@Composable
fun HighScoresScreen() {
    var scores by remember { mutableStateOf<List<Score>>(emptyList()) }

    LaunchedEffect(Unit) {
        getTopScores {
            scores = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🏆 Top 10 Scores", fontSize = 28.sp)

        scores.forEachIndexed { index, score ->
            Text("${index + 1}. ${score.player} - ${score.score}", fontSize = 20.sp)
        }
    }
}
