package sample.models

import kotlin.coroutines.experimental.buildSequence
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by work on 2018/1/16.
 */
data class Obj(val id:Int)

data class BackObj(val id:Int){
    fun fetch():String{
        return "【$id】"
    }
}

//flag = -1表示未处理，占位用；否则是实际index
class CacheObj(){
    var flag = -1
        private set
    var data:String? = null
        private set
    fun update(flag: Int, data:String){
        this.flag = flag
        this.data = data
    }
    fun update(flag: Int){
        this.flag = flag
        this.data = null
    }
    fun dispose(){

    }
}

/**
 * 先简单后复杂
 * 初始化可视块数，后台区间
 * 目前都是数值
 * 核心实现：
 * 1 初始对标，中心值
 * 2 输入偏移量，输出触发，动态计算后续
 */


class CacheManager1(){
    //指向buffer的下标
    data class BufPosition(var beg:Int=-1,var end:Int=-1,var preBeg:Int=-1,var preEnd:Int=-1,var postBeg:Int=-1,var postEnd:Int=-1){
        var flagTick:Int = -1
            private set
        init {
            reset()
        }
        fun tick(flagTick:Int){
            preBeg = -1
            preEnd = -1
            beg = -1
            end = -1
            postBeg = -1
            postEnd = -1
            this.flagTick = flagTick
        }
        fun reset() = tick(-1)
    }
    private lateinit var objArray:List<BackObj>
    private var slideLength:Int = 3
    //3.5*slideLength
    private var buffer: Array<CacheObj> = emptyArray()
    private val bufLock = Any()
    private val bufPosition = BufPosition()

    //region    ZoomListener
    private var onShowListener: ((Int) -> Unit)? = null

    fun setOnShowListener(listener: (centerIndex: Int) -> Unit) {
        //用于外界调整，比如越界处理，范围处理
        onShowListener = listener
    }
    //endregion
    //外部中心
    var index = 0
        private set
    val size:Int get() = buffer.size
    @Volatile
    var canResponse = false
        private set
    var endToEnd: Boolean = false
    fun jump(ind: Int) = if (ind<0) index = 0 else index = ind.rem(size)

    fun bind(objs:List<BackObj>, ind: Int = 0){
        canResponse = false
        if (size>0){
            buffer.forEach { it.dispose() }
            bufPosition.reset()
        }
        objArray = objs
        reset(slideLength, true)
    }
    fun reset(slideLength: Int){
        if (slideLength == this.slideLength) return
        reset(slideLength, false)
    }
    //分配空间
    private fun reset(slideLength:Int, isBind:Boolean) {
        this.slideLength = slideLength
        if (isBind) {
            val totallCount = (3.5F * slideLength).toInt()
            buffer = Array<CacheObj>(totallCount, { CacheObj() })
            index = 0
        }else{
            //调整大小，以中心为准
            //...
            throw NotImplementedError("未实现")
        }
        canResponse = false
        initShow()
        initPre()
    }

    private inline fun getObjInd(ind: Int):Int {
        if (endToEnd){
            if (ind<0) return 0
            return ind.rem(objArray.size)
        }else{
            if (ind !in 0..objArray.size-1) return -1
            return ind
        }
    }
    //初始调用，自动调整中心
    private fun initShow() {
        if (bufPosition.beg == -1) {
            bufPosition.apply {
                beg = (1.5F * slideLength).toInt()
                end = (2.5F * slideLength).toInt() - 1
            }
        }
        for (i in bufPosition.beg..bufPosition.end){
            val objInd = getObjInd(i - bufPosition.beg)
            if (objInd == -1){
                buffer[i].update(i)
            }else{
                buffer[i].update(i,objArray[objInd].fetch())
            }
        }
        index = (bufPosition.beg+bufPosition.end)/2
        onShowListener!!.invoke(index)
    }
    private fun initPre(){
        launch {
            val pBeg = bufPosition.end+1
            val pEnd = pBeg+(1.5F*slideLength).toInt()-1
            for (i in pBeg..pEnd){
                val objInd = getObjInd(i - bufPosition.beg)
                if (objInd == -1){
                    buffer[i].update(i)
                }else{
                    buffer[i].update(i,objArray[objInd].fetch())
                }
            }
            val last = bufPosition.beg-1
            for (i in last downTo 0){
                val objInd = getObjInd(i - bufPosition.beg)
                if (objInd == -1){
                    buffer[i].update(i)
                }else{
                    buffer[i].update(i,objArray[objInd].fetch())
                }
            }
            canResponse = true
        }
    }
}

class PackSystem(count:Int) {
    private val objArray: Array<Obj>

    init {
        objArray = Array<Obj>(count, { i -> Obj(i + 1) })
    }

    var endToEnd: Boolean = false
    val count: Int get() = objArray.size
    var index: Int = 0
        private set
    fun jump(ind: Int) = if (ind<0) index = 0 else index = ind.rem(count)
    val objs = buildSequence<Obj> {
        while (count > 0) {
            if (count == index) {
                if (endToEnd) index = 0
                else return@buildSequence
            }
            yield(objArray[index++])
        }
    }
}