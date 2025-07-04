package com.example.snake

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.material3.Divider

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily


@Composable
fun ScoreScreen(scoreList: List<Score>, onBack: () -> Unit) {
    val customFont = FontFamily(Font(R.font.irish_grover))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCDEAA3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título centrado con margen superior
            Text(
                text = stringResource(R.string.top_scores),
                fontSize = 30.sp,
                fontFamily = customFont,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 40.dp, bottom = 16.dp)
            )

            // Lista con margen horizontal
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(scoreList) { index, score ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${index + 1}. ${score.player}",
                            fontFamily = customFont,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Score: ${score.score}",
                            fontFamily = customFont,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón verde oscuro
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32) // Verde oscuro
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.back),
                    fontFamily = customFont,)
            }
        }
    }
}