package sample.models

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by work on 2018/1/19.
 */

class SimpleMoveManager(val host: SimpleHost, val animationManager: AnimationManager): GestureDetector.OnDoubleTapListener,
        View.OnTouchListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener,AnkoLogger {
    override val loggerTag: String
        get() = "_SiMMr"
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
    private var scrolling = false
    private var scaling = false
    private var enabled = false
    init {
        gestureDetector = GestureDetector(host.context, this)
        scaleGestureDetector = ScaleGestureDetector(host.context, this)
        host.setOnTouchListener(this)
    }
    fun enable(){
        enabled = true
    }
    fun disable(){
        enabled = false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val onTestHandled = host.onTestTap(e)
        if (!onTestHandled){
            //悬浮控件操作
        }
        host.performClick()
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        info { "onDoubleTap:(${e.x},${e.y})" }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent): Boolean {
        animationManager.stopFling()
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
        info { "not implemented = onShowPress" }
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        scrolling = true
        host.moveOffset(distanceX, distanceY)
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        info { "==onFling" }
        animationManager.startFlingAnimation(500,500,velocityX.toInt(),velocityY.toInt(),
                0,1000,0,1000)
        return true
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if(!enabled){
            return false
        }
        var retVal = false
        //retVal = scaleGestureDetector.onTouchEvent(event)
        retVal = gestureDetector.onTouchEvent(event) || retVal
        if(event.action == MotionEvent.ACTION_UP){
            if (scrolling){
                scrolling = false
                onScrollEnd(event)
            }
        }
        return retVal
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        TODO("not implemented")
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        TODO("not implemented")
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        TODO("not implemented")
    }

    private fun onScrollEnd(event: MotionEvent){
        hideHandle()
    }
    private fun hideHandle(){

    }
}



