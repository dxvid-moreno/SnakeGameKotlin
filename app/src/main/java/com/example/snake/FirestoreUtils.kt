package com.example.snake
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.snake.Score


fun uploadScore(player: String, score: Int) {
    val db = FirebaseFirestore.getInstance()

    val scoreData = hashMapOf(
        "player" to player,
        "score" to score,
        "timestamp" to System.currentTimeMillis()
    )
    Log.d("Firestore", "Intentando guardar: $scoreData")
    db.collection("highscores")
        .add(scoreData)
        .addOnSuccessListener {
            Log.d("Firestore", "Puntaje guardado con éxito.")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al guardar el puntaje", e)
        }
}

// Función para obtener el top 10 de puntajes (ordenado de mayor a menor)
fun getTopScores(onResult: (List<Score>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("highscores")
        .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(10)
        .get()
        .addOnSuccessListener { result ->
            val scores = result.map { doc ->
                Score(
                    player = doc.getString("player") ?: "Desconocido",
                    score = doc.getLong("score")?.toInt() ?: 0,
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            onResult(scores)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al obtener puntajes", e)
            onResult(emptyList())
        }
}
