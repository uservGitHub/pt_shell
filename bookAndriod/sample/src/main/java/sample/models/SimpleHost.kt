package sample.models

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * Created by work on 2018/1/18.
 */

/**
 * 简单承载控件
 */
class SimpleHost(ctx:Context):RelativeLayout(ctx) {
    inner class CellManager(val count:Int, backDatas:List<BackGrid>){
        val cacheManager:SimpleCacheManager
        var index: Int
            private set
        var cellMark: Float
        val cellWidth: Float
        fun moveOffset(delta:Float) {
            if (cacheManager.canResponse) {
                cellMark += delta
                val deltaIndex = (cellMark / cellWidth).toInt()
                if (deltaIndex != 0) {
                    //是否要结算
                    info("deltaIndex=$deltaIndex")
                    cellMark = cellMark.rem(cellWidth)
                    cacheManager.locate(index + deltaIndex)
                } else {
                    this@SimpleHost.reDraw()
                }
            }
        }
        fun drawCell(canvas: Canvas){
           val cellSide = cellWidth
            val rect = RectF(0F,0F,cellSide,cellSide)
            rect.offset(-cellMark, this@SimpleHost.height.toFloat()/2)
           cacheManager.objs.forEach {
               info("$rect")
                rectDraw(canvas,
                        strokePaint(Color.BLACK, 4F),
                        rect)
               textDrawCenter(canvas,
                       textPaint(Color.RED, 48F),
                       rect,
                       it.data ?: "null")
               rect.offset(cellWidth, 0F)
           }
        }
        init {
            cacheManager = SimpleCacheManager(count+1, backDatas)
            cacheManager.endToEnd = true
            index = cacheManager.start()
            cellMark = 0F
            cacheManager.setOnShowListener {
                centerIndex: Int ->
                index = centerIndex
                info("index=$index")
                this@SimpleHost.post {
                    this@SimpleHost.reDraw()
                }
            }
            cellWidth = this@SimpleHost.width.toFloat()/count
            info("cellWidth=$cellWidth")
        }
    }
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

        fun rectDraw(canvas: Canvas, strokePaint: Paint, rect: RectF) {
            canvas.drawRect(rect, strokePaint)
        }

        fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: Any) {
            canvas.drawText("$msg",
                    rect.exactCenterX(), (rect.exactCenterY() + deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }
        fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: RectF, msg: Any) {
            canvas.drawText("$msg",
                    rect.centerX(), (rect.centerY() + deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }
    }
    private val moveManager:SimpleMoveManager
    private val animationManager:AnimationManager
    init {
        setWillNotDraw(false)
        animationManager = AnimationManager(this)
        moveManager = SimpleMoveManager(this, animationManager)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        info("width=$width")
        setup()
    }
    fun onTestTap(e:MotionEvent): Boolean{
        info("onTestTag=(${e.x},${e.y})")
        return true
    }
    private lateinit var cellManager:CellManager
    private var canDrawing = false
    fun setup(){
        if (canDrawing) return
        val backData = List<BackGrid>(100, {index: Int -> BackGrid().apply { id=index } })
        cellManager = CellManager(4, backData)
        canDrawing = true
        info("setup")
        moveManager.enable()
        moveOffset(0F,0F)
    }
    fun moveOffset(deltaX:Float, deltaY:Float){
        cellManager.moveOffset(deltaX)
    }

    private fun showContext(canvas: Canvas) {
        if (!canDrawing) return
        cellManager.drawCell(canvas)
    }

    private fun reDraw(){
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.WHITE)
        info("draw Beg")
        showContext(canvas)
        info("draw End")
    }
}