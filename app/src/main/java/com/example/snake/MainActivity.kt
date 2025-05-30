package com.example.snake

import android.content.Context
import android.os.Bundle
import android.view.View
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
import android.content.Intent
import kotlin.jvm.java
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import kotlin.math.abs

enum class ProviderType{
    BASIC,
    GOOGLE,
}

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvWelcome: TextView
    private lateinit var btnNewGame: Button
    private lateinit var btnLogout: Button
    private lateinit var btnLogin: Button
    private lateinit var btnAbout: Button
    private lateinit var btnChangeLanguage: Button
    private lateinit var snakeViewModel: SnakeViewModel

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("language", "en") ?: "en"
        val context = updateBaseContextLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snakeViewModel = ViewModelProvider(this)[SnakeViewModel::class.java]
        setContentView(R.layout.home_page)

        val btnChangeLanguage = findViewById<Button>(R.id.btnChangeLanguage)
        btnChangeLanguage.setOnClickListener {
            val currentLanguage = getCurrentLanguage()
            val newLanguage = if (currentLanguage == "es") "en" else "es"
            saveLanguage(newLanguage)
            recreate()
        }
    }

    private fun saveLanguage(languageCode: String) {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        preferences.edit().putString("language", languageCode).apply()
    }

    private fun getCurrentLanguage(): String {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return preferences.getString("language", "en") ?: "en"
    }

    private fun updateBaseContextLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    override fun onStart() {
        super.onStart()
        val buttonStart = findViewById<Button>(R.id.btnNewGame)
        auth = FirebaseAuth.getInstance()
        tvWelcome = findViewById(R.id.tvWelcome)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnLogout = findViewById(R.id.btnLogout)
        btnLogin = findViewById(R.id.btnLogin)
        btnAbout = findViewById(R.id.btnAbout)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Usuario autenticado
            val email = currentUser.email ?: "User"
            tvWelcome.text = "Welcome, $email"
            tvWelcome.visibility = View.VISIBLE

            btnNewGame.isEnabled = true
            btnNewGame.visibility = View.VISIBLE

            btnLogout.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
        } else {
            // Usuario no autenticado
            tvWelcome.visibility = View.GONE

            btnNewGame.isEnabled = false
            btnNewGame.visibility = View.GONE

            btnLogout.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnAbout.setOnClickListener {
            setContentView(R.layout.about_us)
            val btnBack = findViewById<Button>(R.id.btnBack)
            btnBack.setOnClickListener {
                recreate()
            }
        }


        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNewGame.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(this, SnakeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (::snakeViewModel.isInitialized) {
            snakeViewModel.pauseGame()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (::snakeViewModel.isInitialized) {
            snakeViewModel.resumeGame()
        }
    }

}

@Composable
fun SnakeGame(viewModel: SnakeViewModel = viewModel()) {
    val context = LocalContext.current
    val snakeVersion by viewModel.snakeVersion
    val snake = viewModel.snake.toList()
    val food by viewModel.food
    val gameOver by viewModel.gameOver
    val direction by viewModel.direction
    val boardSize = 16

    fun goBackToMain() {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        if (context is ComponentActivity) {
            context.finish()
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
                onDirectionChange = viewModel::changeDirection
            )
        }
    }

    if (gameOver) {
        GameOverDialog(
            onRestart = { viewModel.resetGame() },
            onReturnToMain = { goBackToMain() }
        )
    }
}


@Composable
fun GameOverDialog(
    onRestart: () -> Unit,
    onReturnToMain: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { /* Prevent dismiss */ },
        title = { Text(stringResource(R.string.game_over_title)) },
        text = { Text(stringResource(R.string.game_over_message)) },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onRestart) {
                    Text(stringResource(R.string.play_again))
                }
                Button(onClick = onReturnToMain) {
                    Text(stringResource(R.string.return_to_main))
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
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
                        val absDx = abs(dx)
                        val absDy = abs(dy)

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



