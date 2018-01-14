package pdfbook.sample.stages

import android.content.Context
import android.os.Environment
import java.io.File
import java.lang.reflect.Array.getLength
import android.content.Context.STORAGE_SERVICE
import android.os.storage.StorageManager
import android.content.ContentValues.TAG
import android.os.storage.StorageVolume
import android.content.Context.STORAGE_SERVICE





/**
 * Created by Administrator on 2017/12/31.
 */

class StorageUtils{
    companion object {
        @Volatile
        private var _appPdfPath:String? = null
        @Volatile
        private var _outMemory: String? = null
        private var _inMemory =  Environment.getExternalStorageDirectory().toString()
        fun initAppStroage(ctx:Context, appPdfPath:String){
            if (_appPdfPath == null){
                synchronized(StorageUtils::class){
                    if(_appPdfPath == null){
                        _appPdfPath = appPdfPath
                        _outMemory = getOutMemory(ctx) ?: ""
                    }
                }
            }
        }

        val outPdfRoot: String
            get() {
                return "$_outMemory/$_appPdfPath"
            }
        val inPdfRoot: String
            get() = "$_inMemory/$_appPdfPath"
        fun subDirs(dir:String) = File(dir).list { dir, name -> dir.isDirectory }
        fun pdfFilenameFromDir(dir:String) =
                File(dir).listFiles { file ->
                    file.isFile && file.canRead() && file.name.endsWith(".pdf", true) }.map { it.absolutePath }
        private fun getOutMemory(ctx:Context):String?{
            val storageManager = ctx.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            try {
                val storageVolumeClass = Class.forName("android.os.storage.StorageVolume")
                val volumeList = storageManager.javaClass.getMethod("getVolumeList")
                val path = storageVolumeClass!!.getMethod("getPath")
                val removable = storageVolumeClass.getMethod("isRemovable")
                val result = volumeList.invoke(storageManager)
                val length = java.lang.reflect.Array.getLength(result)
                for (i in 0 until length){
                    val storageVolumeElement = java.lang.reflect.Array.get(result, i)
                    val name = path.invoke(storageVolumeElement) as String
                    val canRemove = removable.invoke(storageVolumeElement) as Boolean
                    if (canRemove) {
                        return name
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
}