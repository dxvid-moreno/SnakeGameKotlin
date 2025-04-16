package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGame()
        }
    }
}

@Composable
fun SnakeGame() {
    val boardSize = 16
    var snake by remember { mutableStateOf(listOf(Pair(8, 8))) }
    var direction by remember { mutableStateOf(Pair(1, 0)) }
    var food by remember { mutableStateOf(randomFood(boardSize)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(200)

            val head = snake.first()
            val newHead = Pair(
                (head.first + direction.first + boardSize) % boardSize,
                (head.second + direction.second + boardSize) % boardSize
            )

            val ateFood = newHead == food

            snake = listOf(newHead) + if (ateFood) snake else snake.dropLast(1)

            if (ateFood) {
                // Generar nueva comida que no esté dentro de la serpiente
                do {
                    food = randomFood(boardSize)
                } while (snake.contains(food))
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GameBoard(boardSize, snake, food)
        DirectionButtons { direction = it }
    }
}

@Composable
fun GameBoard(boardSize: Int, snake: List<Pair<Int, Int>>, food: Pair<Int, Int>) {
    val cellSize = 20.dp

    Column {
        for (y in 0 until boardSize) {
            Row {
                for (x in 0 until boardSize) {
                    val pos = Pair(x, y)
                    val color = when {
                        pos == food -> Color.Red
                        snake.contains(pos) -> Color.Green
                        else -> Color.Black
                    }

                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .border(1.dp, Color.Gray)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun DirectionButtons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val size = Modifier.size(64.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        Button(onClick = { onDirectionChange(Pair(0, -1)) }, modifier = size) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Arriba")
        }
        Row {
            Button(onClick = { onDirectionChange(Pair(-1, 0)) }, modifier = size) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Izquierda")
            }
            Spacer(modifier = size)
            Button(onClick = { onDirectionChange(Pair(1, 0)) }, modifier = size) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Derecha")
            }
        }
        Button(onClick = { onDirectionChange(Pair(0, 1)) }, modifier = size) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Abajo")
        }
    }
}

fun randomFood(boardSize: Int): Pair<Int, Int> {
    return Pair(Random.nextInt(boardSize), Random.nextInt(boardSize))
}