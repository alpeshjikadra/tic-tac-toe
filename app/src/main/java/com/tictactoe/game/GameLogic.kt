package com.tictactoe.game

enum class Player {
    X, O, NONE
}

data class GameState(
    val board: Array<Array<Player>> = Array(3) { Array(3) { Player.NONE } },
    val currentPlayer: Player = Player.X,
    val gameOver: Boolean = false,
    val winner: Player = Player.NONE,
    val winningLine: WinningLine? = null,
    val scoreX: Int = 0,
    val scoreO: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (!board.contentDeepEquals(other.board)) return false
        if (currentPlayer != other.currentPlayer) return false
        if (gameOver != other.gameOver) return false
        if (winner != other.winner) return false
        if (winningLine != other.winningLine) return false
        if (scoreX != other.scoreX) return false
        if (scoreO != other.scoreO) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + gameOver.hashCode()
        result = 31 * result + winner.hashCode()
        result = 31 * result + (winningLine?.hashCode() ?: 0)
        result = 31 * result + scoreX
        result = 31 * result + scoreO
        return result
    }
}

data class WinningLine(
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    val type: LineType
)

enum class LineType {
    HORIZONTAL, VERTICAL, DIAGONAL_LEFT, DIAGONAL_RIGHT
}

class GameLogic {
    private var gameState = GameState()

    fun getCurrentState(): GameState = gameState

    fun makeMove(row: Int, col: Int): Boolean {
        if (gameState.gameOver || gameState.board[row][col] != Player.NONE) {
            return false
        }

        // Make the move
        val newBoard = gameState.board.map { it.clone() }.toTypedArray()
        newBoard[row][col] = gameState.currentPlayer

        // Check for win
        val winResult = checkWin(newBoard)
        val isWin = winResult.first
        val winningLine = winResult.second

        if (isWin) {
            val newScoreX = if (gameState.currentPlayer == Player.X) gameState.scoreX + 1 else gameState.scoreX
            val newScoreO = if (gameState.currentPlayer == Player.O) gameState.scoreO + 1 else gameState.scoreO
            
            gameState = gameState.copy(
                board = newBoard,
                gameOver = true,
                winner = gameState.currentPlayer,
                winningLine = winningLine,
                scoreX = newScoreX,
                scoreO = newScoreO
            )
        } else if (isBoardFull(newBoard)) {
            // Draw
            gameState = gameState.copy(
                board = newBoard,
                gameOver = true,
                winner = Player.NONE
            )
        } else {
            // Continue game
            val nextPlayer = if (gameState.currentPlayer == Player.X) Player.O else Player.X
            gameState = gameState.copy(
                board = newBoard,
                currentPlayer = nextPlayer
            )
        }

        return true
    }

    fun resetGame() {
        gameState = gameState.copy(
            board = Array(3) { Array(3) { Player.NONE } },
            currentPlayer = Player.X,
            gameOver = false,
            winner = Player.NONE,
            winningLine = null
        )
    }

    fun resetScore() {
        gameState = gameState.copy(
            scoreX = 0,
            scoreO = 0
        )
        resetGame()
    }

    private fun checkWin(board: Array<Array<Player>>): Pair<Boolean, WinningLine?> {
        val currentPlayer = gameState.currentPlayer

        // Check rows
        for (row in 0..2) {
            if (board[row][0] == currentPlayer && 
                board[row][1] == currentPlayer && 
                board[row][2] == currentPlayer) {
                return Pair(true, WinningLine(row, 0, row, 2, LineType.HORIZONTAL))
            }
        }

        // Check columns
        for (col in 0..2) {
            if (board[0][col] == currentPlayer && 
                board[1][col] == currentPlayer && 
                board[2][col] == currentPlayer) {
                return Pair(true, WinningLine(0, col, 2, col, LineType.VERTICAL))
            }
        }

        // Check diagonal (top-left to bottom-right)
        if (board[0][0] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][2] == currentPlayer) {
            return Pair(true, WinningLine(0, 0, 2, 2, LineType.DIAGONAL_LEFT))
        }

        // Check diagonal (top-right to bottom-left)
        if (board[0][2] == currentPlayer && 
            board[1][1] == currentPlayer && 
            board[2][0] == currentPlayer) {
            return Pair(true, WinningLine(0, 2, 2, 0, LineType.DIAGONAL_RIGHT))
        }

        return Pair(false, null)
    }

    private fun isBoardFull(board: Array<Array<Player>>): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == Player.NONE) {
                    return false
                }
            }
        }
        return true
    }
}
