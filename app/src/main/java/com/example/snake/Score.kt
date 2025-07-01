package com.example.snake

data class Score(
    val player: String = "Anonymous",
    val score: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
