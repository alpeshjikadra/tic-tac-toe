package com.tictactoe.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.tictactoe.game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameLogic: GameLogic
    private lateinit var vibrator: Vibrator
    private lateinit var soundManager: SoundManager
    private lateinit var particleSystem: ParticleSystem
    private val gameCells = Array(3) { Array<CustomGameCell?>(3) { null } }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeGame()
        setupUI()
        setupClickListeners()
    }

    private fun initializeGame() {
        gameLogic = GameLogic()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        soundManager = SoundManager(this)
        createParticleSystem()
        createGameBoard()
        updateUI()
    }

    private fun createParticleSystem() {
        particleSystem = ParticleSystem(this)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.gameContainer.addView(particleSystem, layoutParams)
    }
    
    private fun setupUI() {
        // Animate title entrance
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = -100f
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(BounceInterpolator())
            .start()
            
        // Animate score layout entrance
        binding.scoreLayout.alpha = 0f
        binding.scoreLayout.translationX = -200f
        binding.scoreLayout.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(600)
            .setStartDelay(200)
            .setInterpolator(OvershootInterpolator())
            .start()
            
        // Animate game board entrance
        binding.gameContainer.alpha = 0f
        binding.gameContainer.scaleX = 0.8f
        binding.gameContainer.scaleY = 0.8f
        binding.gameContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(700)
            .setStartDelay(400)
            .setInterpolator(OvershootInterpolator())
            .start()
            
        // Animate buttons entrance
        binding.buttonLayout.alpha = 0f
        binding.buttonLayout.translationY = 100f
        binding.buttonLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(600)
            .setInterpolator(BounceInterpolator())
            .start()
    }
    
    private fun createGameBoard() {
        binding.gameBoard.removeAllViews()

        for (row in 0..2) {
            for (col in 0..2) {
                val cell = CustomGameCell(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                        setMargins(8, 8, 8, 8)
                    }

                    onCellClickListener = { onCellClick(row, col) }
                }

                gameCells[row][col] = cell
                binding.gameBoard.addView(cell)

                // Animate cell entrance with staggered timing
                cell.alpha = 0f
                cell.scaleX = 0f
                cell.scaleY = 0f
                cell.rotation = 180f

                cell.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(0f)
                    .setDuration(500)
                    .setStartDelay((row * 3 + col) * 80L + 800)
                    .setInterpolator(OvershootInterpolator(1.2f))
                    .start()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnNewGame.setOnClickListener {
            animateButtonClick(it)
            vibrateDevice(50)
            soundManager.playButtonSound()
            newGame()
        }

        binding.btnResetScore.setOnClickListener {
            animateButtonClick(it)
            vibrateDevice(50)
            soundManager.playButtonSound()
            resetScore()
        }
    }
    
    private fun onCellClick(row: Int, col: Int) {
        val gameState = gameLogic.getCurrentState()
        val cell = gameCells[row][col] ?: return

        if (gameState.gameOver || !cell.isEmpty()) {
            vibrateDevice(100) // Error vibration
            soundManager.playErrorSound()
            animateErrorFeedback(cell)
            return
        }

        vibrateDevice(30) // Success vibration
        soundManager.playMoveSound()

        if (gameLogic.makeMove(row, col)) {
            cell.setPlayer(gameState.currentPlayer, animate = true)
            updateUI()

            val newState = gameLogic.getCurrentState()
            if (newState.gameOver) {
                handleGameEnd(newState)
            }
        }
    }

    private fun animateErrorFeedback(cell: CustomGameCell) {
        // Shake animation for invalid moves
        val shakeAnimation = ObjectAnimator.ofFloat(cell, "translationX", 0f, -20f, 20f, -10f, 10f, 0f)
        shakeAnimation.duration = 300
        shakeAnimation.start()
    }


    
    private fun updateUI() {
        val gameState = gameLogic.getCurrentState()
        
        // Update scores
        binding.tvScoreX.text = getString(R.string.score_x, gameState.scoreX)
        binding.tvScoreO.text = getString(R.string.score_o, gameState.scoreO)
        
        // Update current turn
        if (!gameState.gameOver) {
            val currentPlayerText = if (gameState.currentPlayer == Player.X) 
                getString(R.string.player_x) else getString(R.string.player_o)
            binding.tvCurrentTurn.text = getString(R.string.current_turn, currentPlayerText)
            binding.tvCurrentTurn.setTextColor(ContextCompat.getColor(this,
                if (gameState.currentPlayer == Player.X) R.color.player_x_color else R.color.player_o_color))
        }
    }
    
    private fun handleGameEnd(gameState: GameState) {
        vibrateDevice(200) // Game end vibration

        // Play appropriate sound
        when (gameState.winner) {
            Player.X, Player.O -> {
                soundManager.playWinSound()
                soundManager.playCelebrationSequence()
            }
            Player.NONE -> soundManager.playDrawSound()
        }

        // Show game status
        val statusText = when (gameState.winner) {
            Player.X -> getString(R.string.player_x_wins)
            Player.O -> getString(R.string.player_o_wins)
            Player.NONE -> getString(R.string.game_draw)
        }

        binding.tvGameStatus.text = statusText
        binding.tvGameStatus.setTextColor(ContextCompat.getColor(this,
            when (gameState.winner) {
                Player.X -> R.color.player_x_color
                Player.O -> R.color.player_o_color
                Player.NONE -> R.color.text_secondary
            }))

        // Animate status message
        animateGameEndMessage()

        // Handle winning line and celebrations
        gameState.winningLine?.let {
            highlightWinningCells(it)
            // Create celebration effects
            createWinCelebration()
        }

        // Update current turn text
        binding.tvCurrentTurn.text = getString(R.string.game_over)
        binding.tvCurrentTurn.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
    }

    private fun createWinCelebration() {
        // Create particle explosion at center of game board
        val centerX = binding.gameBoard.width / 2f
        val centerY = binding.gameBoard.height / 2f
        particleSystem.createWinExplosion(centerX, centerY)

        // Add celebration particles falling from top
        particleSystem.createCelebrationEffect()
    }
    
    private fun animateGameEndMessage() {
        binding.tvGameStatus.alpha = 0f
        binding.tvGameStatus.scaleX = 0f
        binding.tvGameStatus.scaleY = 0f
        
        binding.tvGameStatus.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(BounceInterpolator())
            .start()
    }
    
    private fun highlightWinningCells(winningLine: WinningLine) {
        val cells = getWinningCells(winningLine)

        cells.forEach { (row, col) ->
            val cell = gameCells[row][col]
            cell?.setWinning(true)
        }
    }
    
    private fun getWinningCells(winningLine: WinningLine): List<Pair<Int, Int>> {
        return when (winningLine.type) {
            LineType.HORIZONTAL -> listOf(
                Pair(winningLine.startRow, 0),
                Pair(winningLine.startRow, 1),
                Pair(winningLine.startRow, 2)
            )
            LineType.VERTICAL -> listOf(
                Pair(0, winningLine.startCol),
                Pair(1, winningLine.startCol),
                Pair(2, winningLine.startCol)
            )
            LineType.DIAGONAL_LEFT -> listOf(
                Pair(0, 0), Pair(1, 1), Pair(2, 2)
            )
            LineType.DIAGONAL_RIGHT -> listOf(
                Pair(0, 2), Pair(1, 1), Pair(2, 0)
            )
        }
    }
    
    private fun newGame() {
        binding.tvGameStatus.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        particleSystem.stopAnimation()
        gameLogic.resetGame()
        clearBoard()
        updateUI()
    }

    private fun resetScore() {
        binding.tvGameStatus.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        particleSystem.stopAnimation()
        gameLogic.resetScore()
        clearBoard()
        updateUI()

        // Animate score reset
        animateScoreReset()
    }

    private fun clearBoard() {
        for (row in 0..2) {
            for (col in 0..2) {
                val cell = gameCells[row][col]
                cell?.let {
                    it.setWinning(false)
                    it.animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setDuration(150)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                it.reset()
                                it.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .setInterpolator(BounceInterpolator())
                                    .start()
                            }
                        })
                        .start()
                }
            }
        }
    }
    
    private fun animateScoreReset() {
        val scoreXAnim = ObjectAnimator.ofFloat(binding.tvScoreX, "scaleX", 1f, 1.2f, 1f)
        val scoreYAnim = ObjectAnimator.ofFloat(binding.tvScoreO, "scaleX", 1f, 1.2f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scoreXAnim, scoreYAnim)
        animatorSet.duration = 300
        animatorSet.start()
    }
    
    private fun animateButtonClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
    
    private fun vibrateDevice(duration: Long) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
        particleSystem.stopAnimation()
    }
}
