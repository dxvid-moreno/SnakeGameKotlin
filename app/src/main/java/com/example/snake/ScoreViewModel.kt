package com.example.snake

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreViewModel : ViewModel() {

    private val _scoreList = MutableStateFlow<List<Score>>(emptyList())
    val scoreList: StateFlow<List<Score>> = _scoreList

    fun loadScores() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("highscores")
                    .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .await()

                val scores = snapshot.map { doc ->
                    Score(
                        player = doc.getString("player") ?: "Desconocido",
                        score = doc.getLong("score")?.toInt() ?: 0,
                        timestamp = try {
                            doc.getLong("timestamp") ?: (doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L)
                        } catch (e: Exception) {
                            0L
                        }
                    )
                }
                println("SCORES LOADED: $scores")
                _scoreList.value = scores
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}