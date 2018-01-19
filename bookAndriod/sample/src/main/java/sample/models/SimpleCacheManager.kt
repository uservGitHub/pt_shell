package sample.models

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by work on 2018/1/18.
 */

class BackGrid(){
    var id:Int = -1
    fun getData():String{
        return "【$id】"
    }
}
class CacheGird(){
    var key:Int = -1    //List<BackGrid> 索引号
    var data:String? = null
        private set
    fun update(data:String){
        this.data = data
    }
}

/**
 * 1 初始对标，输出中心值，左对齐
 * val index = .start()
 * 2 输入偏移量，输出触发，异步计算后续，完毕解锁
 */
class SimpleCacheManager(val visCount:Int,private val backObjs: List<BackGrid>) {
    //全部是索引值
    private data class Pos(var beg: Int, var end: Int) {
        val ind: Int get() = (beg + end) / 2
    }

    private val ERRORKEY = -100
    private val pos: Pos
    private val showPos: Pos
    private val halfCount: Int
    private val cacheBuf: Array<CacheGird>

    init {
        if (visCount.rem(2) == 0 || visCount < 2) {
            throw IllegalArgumentException("输入参数错误")
        }
        halfCount = (1.5F * visCount).toInt()
        pos = Pos(-1, -1)
        showPos = pos.copy()
        cacheBuf = Array<CacheGird>(halfCount + visCount + halfCount, { CacheGird() })
    }

    //region    fetch
    private inline fun getKey(ind: Int): Int {
        val size = backObjs.size
        if (ind < 0) {
            if (endToEnd) {
                return (ind + size).rem(size)
            } else {
                return ERRORKEY
            }
        } else {
            return ind.rem(size)
        }
    }
    private inline fun fetch(position: Int, key:Int) {
        //Thread.sleep(100)
        val ind = if (position < 0) position + cacheBuf.size else position
        cacheBuf[ind.rem(cacheBuf.size)].apply {
            update(backObjs[key].getData())
            this.key = key
        }
    }
    //endregion

    var endToEnd: Boolean = false
    @Volatile
    var canResponse = false
        private set
    //region    ShowListener
    private var onShowListener: ((Int) -> Unit)? = null

    fun setOnShowListener(listener: (centerIndex: Int) -> Unit) {
        //用于外界调整，比如越界处理，范围处理
        onShowListener = listener
    }

    //endregion
    //region    start
    fun start(): Int {
        //左对齐，默认在中心
        pos.apply {
            beg = 0
            end = visCount - 1
        }
        for (i in -1 downTo -halfCount) {
            val key = getKey(i)
            if (key == ERRORKEY)break
            fetch(i,key)
        }
        for (i in 0 until visCount) {
            val key = getKey(i)
            if (key == ERRORKEY)break
            fetch(i,key)
        }
        for (i in visCount..visCount + halfCount - 1) {
            val key = getKey(i)
            if (key == ERRORKEY)break
            fetch(i,key)
        }
        canResponse = true
        return pos.ind
    }

    //endregion
    //region    locate
    fun locate(ind: Int) {
        if (!canResponse) {
            return
        }

        if (ind !in pos.beg - halfCount..pos.end + halfCount) {
            throw IllegalArgumentException("不在范围内")
        }
        val delta = ind - pos.ind
        showPos.apply {
            beg = pos.beg+delta
            end = pos.end+delta
        }
        onShowListener!!.invoke(pos.ind)
        if (delta == 0) {
            return
        }
        updateCach(delta)
    }

    private fun updateCach(delta: Int) {
        canResponse = false
        launch {
            if (delta > 0) {
                val tailBeg = pos.end + halfCount + 1
                val tailEnd = tailBeg + delta - 1
                //[]
                for (i in tailBeg..tailEnd) {
                    val key = getKey(i)
                    if (key == ERRORKEY)break
                    fetch(i,key)
                }
            } else {
                val headEnd = pos.beg - halfCount - 1
                val headBeg = headEnd + delta + 1
                //[]
                for (i in headEnd downTo headBeg) {
                    val key = getKey(i)
                    if (key == ERRORKEY)break
                    fetch(i,key)
                }
            }
            pos.beg += delta
            pos.end += delta
            canResponse = true
        }
    }
    //endregion
    val objs = buildSequence<CacheGird> {
        val length = cacheBuf.size
        for (i in showPos.beg..showPos.end) {
            if (i < 0) {
                if (endToEnd) {
                    if (i.rem(length) == 0) {
                        yield(cacheBuf[0])
                    } else {
                        yield(cacheBuf[(1 - i / length) * length + i])
                    }
                }
            } else {
                yield(cacheBuf[i.rem(length)])
            }
        }
    }
}