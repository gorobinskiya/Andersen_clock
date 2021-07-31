package com.example.andersen_clock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import java.lang.Math.PI
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import java.util.Calendar as Calendar1

class ClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private val blackPaint: Paint
    private val redPaint: Paint
    private val blackPaint2: Paint
    private val blackPaint3: Paint
    private val textPaint: Paint
    private var hour: Int? = null
    private var minute: Int? = null
    private var second: Int? = null
    private val textArray = arrayOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    private var refreshThread: Thread? = null
    private var mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    invalidate()
                }
            }

        }
    }


    companion object {
        private const val DEFAULT_WIDTH = 200
    }


    init {

        blackPaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.STROKE
        }

        blackPaint2 = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 10f
        }

        blackPaint3 = Paint().apply {
            color = Color.BLACK
            strokeWidth = 20f
            isAntiAlias = true
            style = Paint.Style.STROKE
        }

        redPaint = Paint().apply {
            color = Color.RED
            strokeWidth = 5f
            isAntiAlias = true
        }

        textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        getCurrentTime()

        drawOuterCircle(canvas)

        drawScale(canvas)

        drawTimeText(canvas)

        drawHand(canvas)

        drawCenter(canvas)
    }

    private fun getCurrentTime() {
        val calendar = Calendar1.getInstance()
        hour = calendar.get(Calendar1.HOUR)
        minute = calendar.get(Calendar1.MINUTE)
        second = calendar.get(Calendar1.SECOND)
    }

    private fun drawOuterCircle(canvas: Canvas) {
        canvas.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            (measuredWidth / 2 - 1).toFloat(),
            blackPaint
        )
    }

    private fun drawHand(canvas: Canvas) {
        drawSecond(canvas, redPaint)
        drawHour(canvas, blackPaint3)
        drawMinute(canvas, blackPaint2)
    }

    private fun drawScale(canvas: Canvas) {

        var scaleLength: Float?
        canvas.save()
        for (i in 0..59) {
            if (i % 5 == 0) {
                blackPaint.strokeWidth = 5f
                scaleLength = 30f
            } else {
                blackPaint.strokeWidth = 3f
                scaleLength = 20f
            }
            canvas.drawLine(
                measuredWidth / 2.toFloat(),
                5f,
                measuredWidth / 2.toFloat(),
                (5 + scaleLength),
                blackPaint
            )
            canvas.rotate(
                360 / 60.toFloat(),
                measuredWidth / 2.toFloat(),
                measuredHeight / 2.toFloat()
            )
        }
        canvas.restore()
    }

    private fun drawCenter(canvas: Canvas) {
        canvas.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            20f,
            blackPaint2
        )
    }

    private fun drawTimeText(canvas: Canvas) {
        val textR = (measuredWidth / 2 - 70).toFloat()
        for (i in 0..11) {
            val startX =
                (measuredWidth / 2 + textR * sin(PI / 6 * i) - textPaint.measureText(textArray[i]) / 2).toFloat()
            val startY =
                (measuredHeight / 2 - textR * cos(PI / 6 * i) + textPaint.measureText(textArray[i]) / 2).toFloat()
            canvas.drawText(textArray[i], startX, startY, textPaint)
        }
    }

    private fun drawSecond(canvas: Canvas, paint: Paint) {
        val longR = measuredWidth / 2 - 60
        val shortR = 60
        val startX = (measuredWidth / 2 - shortR * Math.sin(second!!.times(Math.PI / 30))).toFloat()
        val startY = (measuredWidth / 2 + shortR * Math.cos(second!!.times(Math.PI / 30))).toFloat()
        val endX = (measuredWidth / 2 + longR * Math.sin(second!!.times(Math.PI / 30))).toFloat()
        val endY = (measuredWidth / 2 - longR * Math.cos(second!!.times(Math.PI / 30))).toFloat()
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    private fun drawMinute(canvas: Canvas, paint: Paint) {
        val longR = measuredWidth / 2 - 120
        val shortR = 50
        val startX = (measuredWidth / 2 - shortR * Math.sin(minute!!.times(Math.PI / 30))).toFloat()
        val startY = (measuredWidth / 2 + shortR * Math.cos(minute!!.times(Math.PI / 30))).toFloat()
        val endX = (measuredWidth / 2 + longR * Math.sin(minute!!.times(Math.PI / 30))).toFloat()
        val endY = (measuredWidth / 2 - longR * Math.cos(minute!!.times(Math.PI / 30))).toFloat()
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    private fun drawHour(canvas: Canvas, paint: Paint) {
        val longR = measuredWidth / 2 - 300
        val shortR = 40
        val startX = (measuredWidth / 2 - shortR * Math.sin(hour!!.times(Math.PI / 6))).toFloat()
        val startY = (measuredWidth / 2 + shortR * Math.cos(hour!!.times(Math.PI / 6))).toFloat()
        val endX = (measuredWidth / 2 + longR * Math.sin(hour!!.times(Math.PI / 6))).toFloat()
        val endY = (measuredWidth / 2 - longR * Math.cos(hour!!.times(Math.PI / 6))).toFloat()
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val result =
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                DEFAULT_WIDTH
            } else {
                Math.min(widthSpecSize, heightSpecSize)
            }

        setMeasuredDimension(result, result)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshThread = Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(1000)
                    mHandler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    break
                }
            }
        })
        refreshThread?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
        refreshThread?.interrupt()
    }
}