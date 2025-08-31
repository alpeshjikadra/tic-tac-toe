package com.tictactoe.game

import org.junit.Test
import org.junit.Assert.*

class GameLogicTest {

    @Test
    fun testInitialGameState() {
        val gameLogic = GameLogic()
        val state = gameLogic.getCurrentState()
        
        assertEquals(Player.X, state.currentPlayer)
        assertFalse(state.gameOver)
        assertEquals(Player.NONE, state.winner)
        assertEquals(0, state.scoreX)
        assertEquals(0, state.scoreO)
        
        // Check all cells are empty
        for (row in 0..2) {
            for (col in 0..2) {
                assertEquals(Player.NONE, state.board[row][col])
            }
        }
    }

    @Test
    fun testValidMove() {
        val gameLogic = GameLogic()
        
        assertTrue(gameLogic.makeMove(0, 0))
        val state = gameLogic.getCurrentState()
        
        assertEquals(Player.X, state.board[0][0])
        assertEquals(Player.O, state.currentPlayer)
        assertFalse(state.gameOver)
    }

    @Test
    fun testInvalidMove() {
        val gameLogic = GameLogic()
        
        gameLogic.makeMove(0, 0)
        assertFalse(gameLogic.makeMove(0, 0)) // Same cell
        
        val state = gameLogic.getCurrentState()
        assertEquals(Player.O, state.currentPlayer) // Should still be O's turn
    }

    @Test
    fun testHorizontalWin() {
        val gameLogic = GameLogic()
        
        // X wins top row
        gameLogic.makeMove(0, 0) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(0, 1) // X
        gameLogic.makeMove(1, 1) // O
        gameLogic.makeMove(0, 2) // X wins
        
        val state = gameLogic.getCurrentState()
        assertTrue(state.gameOver)
        assertEquals(Player.X, state.winner)
        assertEquals(1, state.scoreX)
        assertEquals(0, state.scoreO)
        assertNotNull(state.winningLine)
        assertEquals(LineType.HORIZONTAL, state.winningLine?.type)
    }

    @Test
    fun testVerticalWin() {
        val gameLogic = GameLogic()
        
        // O wins left column
        gameLogic.makeMove(0, 1) // X
        gameLogic.makeMove(0, 0) // O
        gameLogic.makeMove(1, 1) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(0, 2) // X
        gameLogic.makeMove(2, 0) // O wins
        
        val state = gameLogic.getCurrentState()
        assertTrue(state.gameOver)
        assertEquals(Player.O, state.winner)
        assertEquals(0, state.scoreX)
        assertEquals(1, state.scoreO)
        assertEquals(LineType.VERTICAL, state.winningLine?.type)
    }

    @Test
    fun testDiagonalWin() {
        val gameLogic = GameLogic()
        
        // X wins diagonal
        gameLogic.makeMove(0, 0) // X
        gameLogic.makeMove(0, 1) // O
        gameLogic.makeMove(1, 1) // X
        gameLogic.makeMove(0, 2) // O
        gameLogic.makeMove(2, 2) // X wins
        
        val state = gameLogic.getCurrentState()
        assertTrue(state.gameOver)
        assertEquals(Player.X, state.winner)
        assertEquals(LineType.DIAGONAL_LEFT, state.winningLine?.type)
    }

    @Test
    fun testDraw() {
        val gameLogic = GameLogic()
        
        // Create a draw scenario
        gameLogic.makeMove(0, 0) // X
        gameLogic.makeMove(0, 1) // O
        gameLogic.makeMove(0, 2) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(1, 1) // X
        gameLogic.makeMove(2, 0) // O
        gameLogic.makeMove(1, 2) // X
        gameLogic.makeMove(2, 2) // O
        gameLogic.makeMove(2, 1) // X
        
        val state = gameLogic.getCurrentState()
        assertTrue(state.gameOver)
        assertEquals(Player.NONE, state.winner)
        assertNull(state.winningLine)
    }

    @Test
    fun testResetGame() {
        val gameLogic = GameLogic()
        
        // Play some moves
        gameLogic.makeMove(0, 0)
        gameLogic.makeMove(1, 1)
        
        gameLogic.resetGame()
        val state = gameLogic.getCurrentState()
        
        assertEquals(Player.X, state.currentPlayer)
        assertFalse(state.gameOver)
        assertEquals(Player.NONE, state.winner)
        
        // Check all cells are empty
        for (row in 0..2) {
            for (col in 0..2) {
                assertEquals(Player.NONE, state.board[row][col])
            }
        }
    }

    @Test
    fun testResetScore() {
        val gameLogic = GameLogic()
        
        // Simulate a win to increase score
        gameLogic.makeMove(0, 0) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(0, 1) // X
        gameLogic.makeMove(1, 1) // O
        gameLogic.makeMove(0, 2) // X wins
        
        gameLogic.resetScore()
        val state = gameLogic.getCurrentState()
        
        assertEquals(0, state.scoreX)
        assertEquals(0, state.scoreO)
        assertEquals(Player.X, state.currentPlayer)
        assertFalse(state.gameOver)
    }

    @Test
    fun testMultipleGames() {
        val gameLogic = GameLogic()
        
        // First game - X wins
        gameLogic.makeMove(0, 0) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(0, 1) // X
        gameLogic.makeMove(1, 1) // O
        gameLogic.makeMove(0, 2) // X wins
        
        assertEquals(1, gameLogic.getCurrentState().scoreX)
        
        // Second game - O wins
        gameLogic.resetGame()
        gameLogic.makeMove(0, 1) // X
        gameLogic.makeMove(0, 0) // O
        gameLogic.makeMove(1, 1) // X
        gameLogic.makeMove(1, 0) // O
        gameLogic.makeMove(0, 2) // X
        gameLogic.makeMove(2, 0) // O wins
        
        val finalState = gameLogic.getCurrentState()
        assertEquals(1, finalState.scoreX)
        assertEquals(1, finalState.scoreO)
    }
}
