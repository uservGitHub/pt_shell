package pdfbook.sample.stages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewManager
import lib.book.refLibrary.DrawLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onClick
import pdfbook.sample.stages.ScreenUtils
import pdfbook.sample.stages.StorageUtils
import sample.models.SimpleHost

/**
 * Created by Administrator on 2017/12/31.
 */

class TestSimpleHost : AppCompatActivity(){
    private lateinit var targetView: SimpleHost
    private inline fun ViewManager.simpleHost() = simpleHost(){}
    private inline fun ViewManager.simpleHost(init: SimpleHost.()->Unit) =
            ankoView({ SimpleHost(it) },0,init)
    private var isLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenUtils.notitle(this)
        super.onCreate(savedInstanceState)
        verticalLayout {
            linearLayout {
                /*button("选择文件夹"){
                    onClick {
                        val pdfDirs = StorageUtils.subDirs(StorageUtils.inPdfRoot)
                        selector("Select Dir ?", pdfDirs.toList(), { _, i ->
                            //targetView.load(pdfFilenameFromDir(pdfDirs[i]))
                            var names = StorageUtils.pdfFilenameFromDir("${StorageUtils.inPdfRoot}/${pdfDirs[i]}")
                            if(names.size>1 && names.size.rem(2)==0){
                                names = names.take(names.size-1)
                            }
                            targetView.load(names)
                            isLoaded = true
                        })
                    }
                }
                button("Zoom") {
                    val arr = arrayOf(2.0F, 1.5F,1F,0.8F)
                    onClick {
                        selector("ZoomTo", arr.map { it.toString() }, { _, i ->

                        })
                    }
                }
                button("FitZoom") {
                    val arr = arrayOf("自适应全宽", "自适应全高", "自适应页宽", "自适应页高")
                    onClick {
                        selector("Can Use AutoZoom  ?", arr.map { it.toString() }, { _, i ->
                            when(i){
                                //0 -> targetView.backMap.fitFullWidth = true
                                //1 -> targetView.backMap.fitFullHeight = true
                                //2 -> targetView.fitWidthPageToScreen()
                                //3 -> targetView.fitHeightPageToScreen()
                            }
                            //targetView.invalidate()
                        })
                    }
                }*/
                button("Setup"){
                    onClick {
                        targetView.setup()
                    }
                }
                button("MoveTo") {
                    val arr = arrayOf(10, 15, 20, -10, -15, -20)
                    onClick {
                        selector("Can Use Delta Move X?", arr.map { it.toString() }, { _, i ->
                            targetView.moveOffset(arr[i].toFloat(), 0F)
                        })
                    }
                }

            }.lparams(width= matchParent)
            targetView = simpleHost() {
                //setup()
            }.lparams(width = matchParent, height = dip(0),weight = 1F)
        }.apply {
            //targetView.setup()
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenUtils.fullScreen(this)
    }
}