package com.example.snake

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SnakeViewModel : ViewModel() {

    private val boardSize = 16
    private var gameLoopJob: Job? = null

    var snake = mutableStateListOf(Pair(8, 8))
        private set

    var snakeVersion = mutableStateOf(0) // 🔁 Usado para forzar recomposición

    var direction = mutableStateOf(Pair(1, 0))
        private set

    var food = mutableStateOf(randomFood())
        private set

    var gameOver = mutableStateOf(false)
        private set

    init {
        startGameLoop()
    }

    fun pauseGame() {
        gameLoopJob?.cancel()
    }

    fun resumeGame() {
        if (!gameOver.value) {
            startGameLoop()
        }
    }

    fun changeDirection(newDir: Pair<Int, Int>) {
        val current = direction.value
        if (newDir.first + current.first != 0 || newDir.second + current.second != 0) {
            direction.value = newDir
        }
    }

    fun resetGame() {
        snake.clear()
        snake.add(Pair(8, 8))
        direction.value = Pair(1, 0)
        food.value = randomFood()
        gameOver.value = false
        snakeVersion.value++ // 🔁 Forzar recomposición
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (!gameOver.value) {
                delay(200)
                val head = snake.first()
                val newHead = Pair(
                    head.first + direction.value.first,
                    head.second + direction.value.second
                )

                if (snake.contains(newHead) ||
                    newHead.first !in 0 until boardSize ||
                    newHead.second !in 0 until boardSize) {
                    gameOver.value = true
                    break
                }

                val ateFood = newHead == food.value
                val newSnake = mutableListOf(newHead)
                newSnake.addAll(if (ateFood) snake else snake.dropLast(1))
                snake.clear()
                snake.addAll(newSnake)
                snakeVersion.value++

                if (ateFood) {
                    do {
                        food.value = randomFood()
                    } while (snake.contains(food.value))
                }
            }
        }
    }

    private fun randomFood(): Pair<Int, Int> {
        return Pair(Random.nextInt(boardSize), Random.nextInt(boardSize))
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}

