package com.tictactoe.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat

class CustomGameCell @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var player: Player = Player.NONE
    private var animationProgress = 0f
    private var glowProgress = 0f
    private var isWinning = false
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }
    
    private val xPath = Path()
    private val oPath = Path()
    private val backgroundRect = RectF()
    
    var onCellClickListener: (() -> Unit)? = null
    
    init {
        setOnClickListener { onCellClickListener?.invoke() }
        isClickable = true
        isFocusable = true
        
        // Set background color
        backgroundPaint.color = ContextCompat.getColor(context, R.color.cell_background)
    }
    
    fun setPlayer(newPlayer: Player, animate: Boolean = true) {
        if (player == newPlayer) return
        
        player = newPlayer
        
        if (animate && newPlayer != Player.NONE) {
            animatePlayerPlacement()
        } else {
            animationProgress = if (newPlayer == Player.NONE) 0f else 1f
            invalidate()
        }
    }
    
    fun setWinning(winning: Boolean) {
        isWinning = winning
        if (winning) {
            animateWinningGlow()
        } else {
            glowProgress = 0f
            invalidate()
        }
    }
    
    private fun animatePlayerPlacement() {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600
            interpolator = OvershootInterpolator(1.5f)
            addUpdateListener { animation ->
                animationProgress = animation.animatedValue as Float
                invalidate()
            }
        }
        
        // Add scale animation to the view
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1.1f, 1f)
        val rotation = ObjectAnimator.ofFloat(this, "rotation", -15f, 15f, 0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animator, scaleX, scaleY, rotation)
        animatorSet.start()
    }
    
    private fun animateWinningGlow() {
        val glowAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                glowProgress = animation.animatedValue as Float
                invalidate()
            }
        }
        glowAnimator.start()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val padding = 20f
        backgroundRect.set(padding, padding, w - padding, h - padding)
        
        // Create X path
        xPath.reset()
        val xPadding = w * 0.25f
        xPath.moveTo(xPadding, xPadding)
        xPath.lineTo(w - xPadding, h - xPadding)
        xPath.moveTo(w - xPadding, xPadding)
        xPath.lineTo(xPadding, h - xPadding)
        
        // Create O path
        oPath.reset()
        val oPadding = w * 0.25f
        val centerX = w / 2f
        val centerY = h / 2f
        val radius = (w - oPadding * 2) / 2f
        oPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background with rounded corners
        canvas.drawRoundRect(backgroundRect, 24f, 24f, backgroundPaint)
        
        // Draw glow effect if winning
        if (isWinning && glowProgress > 0) {
            glowPaint.color = ContextCompat.getColor(context, R.color.win_glow)
            glowPaint.alpha = (glowProgress * 100).toInt()
            canvas.drawRoundRect(backgroundRect, 24f, 24f, glowPaint)
        }
        
        // Draw player symbol with animation
        if (player != Player.NONE && animationProgress > 0) {
            when (player) {
                Player.X -> drawAnimatedX(canvas)
                Player.O -> drawAnimatedO(canvas)
                Player.NONE -> { /* Do nothing */ }
            }
        }
    }
    
    private fun drawAnimatedX(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.player_x_color)
        paint.alpha = (animationProgress * 255).toInt()
        
        // Create animated path for X
        val pathMeasure = android.graphics.PathMeasure()
        val animatedPath = Path()
        
        // First line of X
        pathMeasure.setPath(xPath, false)
        val length1 = pathMeasure.length
        pathMeasure.getSegment(0f, length1 * animationProgress, animatedPath, true)
        
        // Second line of X (starts after first line is halfway done)
        if (animationProgress > 0.5f) {
            val secondLineProgress = (animationProgress - 0.5f) * 2f
            val tempPath = Path()
            xPath.reset()
            val xPadding = width * 0.25f
            tempPath.moveTo(width - xPadding, xPadding)
            tempPath.lineTo(xPadding, height - xPadding)
            
            pathMeasure.setPath(tempPath, false)
            val length2 = pathMeasure.length
            pathMeasure.getSegment(0f, length2 * secondLineProgress, animatedPath, true)
        }
        
        canvas.drawPath(animatedPath, paint)
    }
    
    private fun drawAnimatedO(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.player_o_color)
        paint.alpha = (animationProgress * 255).toInt()
        
        // Create animated path for O
        val pathMeasure = android.graphics.PathMeasure(oPath, false)
        val length = pathMeasure.length
        val animatedPath = Path()
        
        pathMeasure.getSegment(0f, length * animationProgress, animatedPath, true)
        canvas.drawPath(animatedPath, paint)
    }
    
    fun reset() {
        player = Player.NONE
        animationProgress = 0f
        glowProgress = 0f
        isWinning = false
        invalidate()
    }
    
    fun isEmpty(): Boolean = player == Player.NONE
    
    fun getPlayer(): Player = player
}
