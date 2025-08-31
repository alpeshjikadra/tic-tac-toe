package com.tictactoe.game

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var position: PointF,
    var velocity: PointF,
    var life: Float,
    var maxLife: Float,
    var size: Float,
    var color: Int,
    var alpha: Float = 1f
)

class ParticleSystem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    
    private val colors = intArrayOf(
        R.color.accent_blue,
        R.color.accent_purple,
        R.color.accent_pink,
        R.color.accent_orange,
        R.color.win_glow
    )
    
    fun createWinExplosion(centerX: Float, centerY: Float) {
        particles.clear()
        
        // Create burst of particles
        repeat(30) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 200 + 100
            val velocity = PointF(
                (cos(angle) * speed).toFloat(),
                (sin(angle) * speed).toFloat()
            )
            
            val particle = Particle(
                position = PointF(centerX, centerY),
                velocity = velocity,
                life = 1f,
                maxLife = 1f,
                size = Random.nextFloat() * 8 + 4,
                color = ContextCompat.getColor(context, colors[Random.nextInt(colors.size)])
            )
            
            particles.add(particle)
        }
        
        startAnimation()
    }
    
    fun createCelebrationEffect() {
        particles.clear()
        
        // Create falling particles from top
        repeat(50) {
            val particle = Particle(
                position = PointF(
                    Random.nextFloat() * width,
                    -20f
                ),
                velocity = PointF(
                    Random.nextFloat() * 40 - 20,
                    Random.nextFloat() * 100 + 50
                ),
                life = 1f,
                maxLife = 1f,
                size = Random.nextFloat() * 6 + 2,
                color = ContextCompat.getColor(context, colors[Random.nextInt(colors.size)])
            )
            
            particles.add(particle)
        }
        
        startAnimation()
    }
    
    private fun startAnimation() {
        animator?.cancel()
        
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }
    
    private fun updateParticles() {
        val deltaTime = 0.016f // Assuming 60 FPS
        
        particles.removeAll { particle ->
            // Update position
            particle.position.x += particle.velocity.x * deltaTime
            particle.position.y += particle.velocity.y * deltaTime
            
            // Apply gravity
            particle.velocity.y += 300 * deltaTime
            
            // Update life
            particle.life -= deltaTime / 2f
            particle.alpha = particle.life.coerceIn(0f, 1f)
            
            // Remove if dead or out of bounds
            particle.life <= 0 || 
            particle.position.y > height + 50 ||
            particle.position.x < -50 ||
            particle.position.x > width + 50
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        particles.forEach { particle ->
            paint.color = particle.color
            paint.alpha = (particle.alpha * 255).toInt()
            
            canvas.drawCircle(
                particle.position.x,
                particle.position.y,
                particle.size,
                paint
            )
        }
    }
    
    fun stopAnimation() {
        animator?.cancel()
        particles.clear()
        invalidate()
    }
}
