import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class SnakeGameViewModel : ViewModel() {
    private val _gameState = mutableStateOf(GameState.RUNNING)
    val gameState = _gameState

    // Aquí irían también otros estados como la posición de la culebra, dirección, etc.

    fun pauseGame() {
        _gameState.value = GameState.PAUSED
    }

    fun resumeGame() {
        _gameState.value = GameState.RUNNING
    }

    fun gameOver() {
        _gameState.value = GameState.GAME_OVER
    }
}
