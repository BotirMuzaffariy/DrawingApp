package uz.lazycoder.drawingapp.customview

import android.os.Looper
import android.os.Handler
import android.graphics.*
import android.content.Context
import android.view.MotionEvent
import android.util.AttributeSet
import uz.lazycoder.drawingapp.R
import androidx.appcompat.widget.AppCompatImageView
import uz.lazycoder.drawingapp.utils.Utils
import uz.lazycoder.drawingapp.models.PointM

class MyImageView : AppCompatImageView, Runnable {

    var b: Bitmap? = null
    private var pLine = Paint()
    private var c: Canvas? = null

    private var duration = 0L
    private var revPosAlg2 = 1
    private var revPosAlg3 = 1
    private var currentPos = -1
    private var currentAlg = -1

    private var isClear = false
    private var list = Utils.list
    private var mainHandler = Handler(Looper.getMainLooper())

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        pLine.strokeWidth = 5f
        pLine.isAntiAlias = true
        pLine.color = Color.BLACK
        pLine.strokeCap = Paint.Cap.ROUND
        pLine.style = Paint.Style.STROKE
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        c = Canvas(b!!)

        currentPos = -1
        Utils.list.clear()
        c?.drawColor(resources.getColor(R.color.iv_color))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> list.add(PointM(touchX.toInt(), touchY.toInt(), true))
            MotionEvent.ACTION_MOVE -> list.add(PointM(touchX.toInt(), touchY.toInt()))
            MotionEvent.ACTION_UP -> list.add(PointM(touchX.toInt(), touchY.toInt()))
            else -> return false
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(b!!, 0f, 0f, Paint())

        if (isClear) {
            c?.drawColor(resources.getColor(R.color.iv_color))
            Utils.list.clear()
            currentPos = -1
            revPosAlg2 = 1
            revPosAlg3 = 1
            isClear = false
        }
    }

    fun startDraw(algorithm: Int, dur: Int) {
        duration = dur.toLong()
        currentAlg = algorithm
        mainHandler.removeCallbacks(this)
        mainHandler.postDelayed(this, 0)
    }

    fun clearDraw() {
        isClear = true
        invalidate()
    }

    fun changeSize(w: Int, h: Int) {
        isClear = true
        background = null
        onSizeChanged(w, h, width, height)
        invalidate()
    }

    override fun run() {
        currentPos++
        val listSize = list.size
        if (currentPos < listSize - 1) {
            when (currentAlg) {
                0 -> {
                    val item = list[currentPos]
                    val nextItem = list[currentPos + 1]

                    if (nextItem.isDown) {
                        c?.drawPoint(item.x.toFloat(), item.y.toFloat(), pLine)
                    } else {
                        c?.drawLine(
                            item.x.toFloat(),
                            item.y.toFloat(),
                            nextItem.x.toFloat(),
                            nextItem.y.toFloat(),
                            pLine
                        )
                    }
                }
                1 -> {
                    val item = list[listSize - revPosAlg2++]
                    val nextItem = list[listSize - revPosAlg2]

                    if (item.isDown) {
                        c?.drawPoint(item.x.toFloat(), item.y.toFloat(), pLine)
                    } else {
                        c?.drawLine(
                            item.x.toFloat(),
                            item.y.toFloat(),
                            nextItem.x.toFloat(),
                            nextItem.y.toFloat(),
                            pLine
                        )
                    }
                }
                2 -> {
                    val item1 = list[currentPos]
                    val nextItem1 = list[currentPos + 1]

                    if (nextItem1.isDown) {
                        c?.drawPoint(item1.x.toFloat(), item1.y.toFloat(), pLine)
                    } else {
                        c?.drawLine(
                            item1.x.toFloat(),
                            item1.y.toFloat(),
                            nextItem1.x.toFloat(),
                            nextItem1.y.toFloat(),
                            pLine
                        )
                    }

                    val item2 = list[listSize - revPosAlg3++]
                    val nextItem2 = list[listSize - revPosAlg3]
                    if (item2.isDown) {
                        c?.drawPoint(item2.x.toFloat(), item2.y.toFloat(), pLine)
                    } else {
                        c?.drawLine(
                            item2.x.toFloat(),
                            item2.y.toFloat(),
                            nextItem2.x.toFloat(),
                            nextItem2.y.toFloat(),
                            pLine
                        )
                    }
                }
            }

            invalidate()
            mainHandler.postDelayed(this, duration)
        } else {
            revPosAlg2 = 1
            revPosAlg3 = 1
        }
    }

}