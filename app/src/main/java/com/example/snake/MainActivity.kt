package com.example.snake

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import android.widget.Button
import androidx.compose.ui.text.font.FontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val buttonStart = findViewById<Button>(R.id.btnNewGame)

        buttonStart.setOnClickListener {
            this.setContent {
                SnakeGame()
            }
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
                do {
                    food = randomFood(boardSize)
                } while (snake.contains(food))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCDEAA3)), // verde claro suave
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "SCORE: ${snake.size - 1}",
            color = Color(0xFF2F4F2F),
            modifier = Modifier.padding(top = 16.dp),

        )

        Box(
            modifier = Modifier
                .border(2.dp, Color.DarkGray)
                .padding(8.dp)
                .background(Color(0xFFCDEAA3)) // igual que fondo
        ) {
            GestureControlledBoard(
                boardSize = boardSize,
                snake = snake,
                food = food,
                onDirectionChange = { newDir ->
                    if (newDir.first + direction.first != 0 || newDir.second + direction.second != 0) {
                        direction = newDir
                    }
                }
            )
        }
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
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun GestureControlledBoard(
    boardSize: Int,
    snake: List<Pair<Int, Int>>,
    food: Pair<Int, Int>,
    onDirectionChange: (Pair<Int, Int>) -> Unit
) {
    val cellSize = 20.dp
    var touchStart by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    Column(
        
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchStart = Pair(offset.x, offset.y)
                    },
                    onDragEnd = {
                        touchStart = null
                    },
                    onDragCancel = {
                        touchStart = null
                    },
                    onDrag = { change, dragAmount ->
                        val (dx, dy) = dragAmount
                        val absDx = kotlin.math.abs(dx)
                        val absDy = kotlin.math.abs(dy)

                        if (absDx > absDy) {
                            onDirectionChange(if (dx > 0) Pair(1, 0) else Pair(-1, 0))
                        } else {
                            onDirectionChange(if (dy > 0) Pair(0, 1) else Pair(0, -1))
                        }
                    }
                )
            }
    ) {
        for (y in 0 until boardSize) {
            Row {
                for (x in 0 until boardSize) {
                    val pos = Pair(x, y)
                    val color = when {
                        pos == food -> Color.Red
                        snake.contains(pos) -> Color(0xFF355E3B) // verde militar
                        else -> Color(0xFFCDEAA3) // fondo claro
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


fun randomFood(boardSize: Int): Pair<Int, Int> {
    return Pair(Random.nextInt(boardSize), Random.nextInt(boardSize))
}