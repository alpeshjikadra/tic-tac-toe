package com.tictactoe.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

class SoundManager(private val context: Context) {
    
    private var soundPool: SoundPool? = null
    private var toneGenerator: ToneGenerator? = null
    private val handler = Handler(Looper.getMainLooper())
    
    // Sound effect IDs
    private var soundMove = 0
    private var soundWin = 0
    private var soundDraw = 0
    private var soundButton = 0
    private var soundError = 0
    
    init {
        initializeSoundPool()
        initializeToneGenerator()
    }
    
    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
    }
    
    private fun initializeToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (e: RuntimeException) {
            // ToneGenerator creation failed, we'll use alternative sounds
            toneGenerator = null
        }
    }
    
    fun playMoveSound() {
        // Play a short, pleasant tone for moves
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
    }
    
    fun playWinSound() {
        // Play a victory fanfare using multiple tones
        toneGenerator?.let { generator ->
            // Play ascending notes
            generator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)
            handler.postDelayed({
                generator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 150)
            }, 100)
            handler.postDelayed({
                generator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 200)
            }, 200)
        }
    }
    
    fun playDrawSound() {
        // Play a neutral tone for draws
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 300)
    }
    
    fun playButtonSound() {
        // Play a subtle click sound for buttons
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
    }
    
    fun playErrorSound() {
        // Play an error sound for invalid moves
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200)
    }
    
    fun playCelebrationSequence() {
        // Play a longer celebration sequence
        toneGenerator?.let { generator ->
            val celebrationTones = intArrayOf(
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,
                ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK,
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE
            )
            
            celebrationTones.forEachIndexed { index, tone ->
                handler.postDelayed({
                    generator.startTone(tone, 150)
                }, index * 100L)
            }
        }
    }
    
    fun release() {
        soundPool?.release()
        soundPool = null
        toneGenerator?.release()
        toneGenerator = null
        handler.removeCallbacksAndMessages(null)
    }
    
    companion object {
        // Tone frequencies for custom sounds
        const val TONE_MOVE = 800
        const val TONE_WIN_LOW = 523   // C5
        const val TONE_WIN_MID = 659   // E5
        const val TONE_WIN_HIGH = 784  // G5
        const val TONE_ERROR = 200
        const val TONE_BUTTON = 1000
    }
}
