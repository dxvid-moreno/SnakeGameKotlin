package com.example.snake

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SnakeUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SnakeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SnakeViewModel()
        viewModel.pauseGame() // evitamos el loop mientras probamos
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `snake initializes correctly`() {
        assertEquals(listOf(Pair(8, 8)), viewModel.snake)
        assertFalse(viewModel.gameOver.value)
    }

    @Test
    fun `snake changes direction and moves`() = runTest {
        viewModel.changeDirection(Pair(0, 1)) // hacia abajo
        viewModel.resumeGame()
        advanceTimeBy(201)
        val newHead = viewModel.snake.first()
        assertEquals(Pair(8, 9), newHead)
    }

    @Test
    fun `snake grows when eating food`() = runTest {
        // colocamos comida frente a la serpiente
        viewModel.food.value = Pair(9, 8)
        viewModel.changeDirection(Pair(1, 0)) // derecha
        viewModel.resumeGame()
        advanceTimeBy(201)
        assertEquals(2, viewModel.snake.size)
    }

    @Test
    fun `game over when snake hits wall`() = runTest {
        viewModel.snake.clear()
        viewModel.snake.add(Pair(15, 8)) // en el borde derecho
        viewModel.direction.value = Pair(1, 0) // hacia la pared
        viewModel.resumeGame()
        advanceTimeBy(201)
        assertTrue(viewModel.gameOver.value)
    }

    @Test
    fun `snake shrinks when eating rotten apple`() = runTest {
        viewModel.snake.clear()
        viewModel.snake.addAll(listOf(Pair(8, 8), Pair(7, 8), Pair(6, 8)))
        viewModel.rottenApples.clear()
        viewModel.rottenApples.add(Pair(9, 8))
        viewModel.changeDirection(Pair(1, 0)) // derecha
        viewModel.resumeGame()
        advanceTimeBy(201)
        // debería haberse comido la podrida y reducir en tamaño
        assertEquals(2, viewModel.snake.size)
    }

    
}
