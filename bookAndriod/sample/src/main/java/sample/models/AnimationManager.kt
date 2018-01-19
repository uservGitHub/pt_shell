package sample.models

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller

/**
 * Created by work on 2018/1/19.
 */

class AnimationManager(val host: SimpleHost){
    private var animation:ValueAnimator? = null
    private var flinging = false
    private val scroller:OverScroller
    init {
        scroller = OverScroller(host.context)
    }
    fun stopAll(){
        animation?.apply {
            cancel()
        }
        animation = null
        stopFling()
    }
    fun stopFling(){
        flinging = false
        scroller.forceFinished(true)
    }
    fun startXAnimation(xFrom:Float, xTo:Float){
        stopAll()
        animation = ValueAnimator.ofFloat(xFrom, xTo)
        val xAnimation = XAnimation()
        animation?.apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(xAnimation)
            addListener(xAnimation)
            setDuration(400)
            start()
        }
    }
    fun startYAnimation(yFrom:Float, yTo:Float){
        stopAll()
        animation = ValueAnimator.ofFloat(yFrom, yTo)
        val yAnimation = YAnimation()
        animation?.apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(yAnimation)
            addListener(yAnimation)
            setDuration(400)
            start()
        }
    }
    fun startFlingAnimation(startX:Int,startY:Int,velocityX:Int,velocityY:Int,minX:Int,maxX:Int,minY:Int,maxY:Int){
        stopAll()
        flinging = true
        scroller.fling(startX,startY,velocityX,velocityY,minX,maxX,minY,maxY)
    }
    internal fun computeFling(){
        if(scroller.computeScrollOffset()){
            host.moveOffset(0F,0F)
            //pdfView.loadPageByOffset()
        }else if (flinging){
            //fling finished
            flinging = false
            //pdfView.loadPages();
            hideHandle()
        }
    }
    private fun hideHandle() {
        /*if (pdfView.getScrollHandle() != null) {
            pdfView.getScrollHandle().hideDelayed()
        }*/
    }
    inner class XAnimation():AnimatorListenerAdapter(),ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            host.moveOffset(offset, 0F)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
        }
    }
    inner class YAnimation():AnimatorListenerAdapter(),ValueAnimator.AnimatorUpdateListener{
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val offset = animation.getAnimatedValue() as Float
            host.moveOffset(0F, offset)
            //pdfView.loadPageByOffset();
        }

        override fun onAnimationCancel(animation: Animator?) {
            //pdfView.loadPages();
        }

        override fun onAnimationEnd(animation: Animator?) {
            //pdfView.loadPages();
        }
    }
}