package com.example.snake.model

data class Score(
    val player: String = "Anonymous",
    val score: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
