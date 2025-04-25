package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import kotlin.jvm.java


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)


    }

    override fun onStart() {
        super.onStart()
        val buttonStart = findViewById<Button>(R.id.btnNewGame)

        buttonStart.setOnClickListener {
            this.setContent {
                SnakeGame()
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onRestart() {
        super.onRestart()

    }
}

@Composable
fun SnakeGame() {
    val boardSize = 16
    var snake by remember { mutableStateOf(listOf(Pair(8, 8))) }
    var direction by remember { mutableStateOf(Pair(1, 0)) }
    var food by remember { mutableStateOf(randomFood(boardSize)) }
    var gameOver by remember { mutableStateOf(false) }

    fun resetGame() {
        snake = listOf(Pair(8, 8))
        direction = Pair(1, 0)
        food = randomFood(boardSize)
        gameOver = false
    }

    LaunchedEffect(gameOver) {
        if (gameOver) return@LaunchedEffect
        while (!gameOver) {
            delay(200)

            val head = snake.first()
            val newHead = Pair(
                head.first + direction.first,
                head.second + direction.second
            )


            if (snake.contains(newHead)) {
                gameOver = true
                break
            }

            val ateFood = newHead == food
            snake = listOf(newHead) + if (ateFood) snake else snake.dropLast(1)

            if (ateFood) {
                do {
                    food = randomFood(boardSize)
                } while (snake.contains(food))
            }

            if (newHead.first !in 0 until boardSize || newHead.second !in 0 until boardSize) {
                gameOver = true
                break
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCDEAA3)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${stringResource(R.string.score)}${snake.size - 1}",
            color = Color(0xFF2F4F2F),
            fontSize = 40.sp,
            modifier = Modifier.padding(top = 16.dp)

        )

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .border(2.dp, Color.Black)
                .padding(2.dp)
                .background(Color(0xFFCDEAA3))
        ) {
            GestureControlledBoard(
                boardSize = boardSize,
                snake = snake,
                food = food,
                currentDirection = direction,
                onDirectionChange = { newDir ->
                    if (newDir.first + direction.first != 0 || newDir.second + direction.second != 0) {
                        direction = newDir
                    }
                }
            )
        }
    }

    if (gameOver) {
        GameOverDialog(onRestart = {resetGame()})
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
    onDirectionChange: (Pair<Int, Int>) -> Unit,
    currentDirection: Pair<Int, Int>
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
                            onDirectionChange(if (dx > 0 && currentDirection!=Pair(-1, 0)) Pair(1, 0) else Pair(-1, 0))
                        } else {
                            onDirectionChange(if (dy > 0 && currentDirection!=Pair(0, -1)) Pair(0, 1) else Pair(0, -1))
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
                            .border(1.dp, Color(0xFFCDEAA3))
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

@Composable
fun GameOverDialog(onRestart: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { /* No hacer nada al cerrar */ },
        title = { Text("Perdiste") },
        text = { Text("¡Has perdido! Intenta nuevamente.") },
        confirmButton = {
            Button(onClick = {
                onRestart()
            }) {
                Text("Volver a jugar")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}