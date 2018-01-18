package sample.utils

/**
 * Created by work on 2018/1/18.
 */
class TimeUtils {
    companion object {
        //计算毫秒数
        fun calcMs(task: () -> Unit, repeatCount: Int = 1): Long {
            val begTick = System.currentTimeMillis()
            var i = 0
            while (i < repeatCount) {
                task()
                i++
            }
            return System.currentTimeMillis() - begTick
        }
    }
}