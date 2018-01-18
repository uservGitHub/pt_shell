package sample.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.widget.RelativeLayout

/**
 * Created by work on 2018/1/18.
 */

/**
 * 简单承载控件
 */
class SimpleHost(ctx:Context):RelativeLayout(ctx) {
    companion object {
        private const val TAG = "_SiHt"
        val emptyPaint = Paint()
        fun info(any: Any?) {
            any?.let {
                Log.i(TAG, any.toString())
            }
        }

        fun assert(result: Boolean) {
            if (!result) {
                Log.i(TAG, "assert false")
            }
        }

        fun deltaCenterHeightFromFont(paint: Paint) =
                paint.fontMetricsInt.let {
                    (it.top + it.bottom) / 2
                }

        fun strokePaint(colorInt: Int, width: Float) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.STROKE
                    strokeWidth = width
                }

        fun textPaint(colorInt: Int, fontSize: Float) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.FILL
                    textAlign = Paint.Align.CENTER
                    textSize = fontSize
                    flags = Paint.ANTI_ALIAS_FLAG
                }

        fun rectDraw(canvas: Canvas, strokePaint: Paint, rect: Rect) {
            canvas.drawRect(rect, strokePaint)
        }

        fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: Any) {
            canvas.drawText("$msg",
                    rect.exactCenterX(), (rect.exactCenterY() + deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }
    }

    init {
        setWillNotDraw(false)
    }
    private lateinit var cacheManager:SimpleCacheManager
    private var index = -1
    private val side = 30
    private val visCount = 5
    private var shockX = 0
    fun setup(){
        val backData = List<BackGrid>(100, {index: Int -> BackGrid().apply { id=index } })
        cacheManager = SimpleCacheManager(visCount, backData)
        index = cacheManager.start()
        cacheManager.setOnShowListener {
            centerIndex: Int ->
            index = centerIndex
            this@SimpleHost.post {
                this@SimpleHost.reDraw()
            }
        }
    }

    fun moveOffset(deltaX:Float, deltaY:Float){
        if (cacheManager.canResponse){
            shockX += deltaX.toInt()
            if (Math.abs(shockX)>=side){
                val delta = (shockX/side).toInt()
                shockX = shockX.rem(side)
                cacheManager.locate(index + delta)
            }
        }
    }

    private fun showContext(canvas: Canvas) {
        val cellWidth = width / visCount
        val cellHeight = height
        val rect = Rect(0, 0, cellWidth, cellHeight)
        cacheManager.objs.forEach {
            //info(it.data)
            textDrawCenter(canvas,
                    textPaint(Color.RED, 48F),
                    rect,
                    it.data ?: "null")
            rect.offset(cellWidth, 0)
        }
    }

    private fun reDraw(){
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.WHITE)
        if(index == -1){
            return
        }
        showContext(canvas)
    }
}