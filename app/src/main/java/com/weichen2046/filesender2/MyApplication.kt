package com.weichen2046.filesender2

import android.app.Application
import android.util.Log

import com.weichen2046.filesender2.service.ServiceManager

/**
 * Created by chenwei on 2017/1/31.
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        ServiceManager.init(this, {
            initialized = true
            Log.d(TAG, "global service connected")
            for (callback in waitList) {
                callback()
            }
            waitList.clear()
        }, {
            initialized = false
            Log.d(TAG, "global service disconnected")
        })
    }

    companion object {

        const val TAG = "MyApplication"

        val waitList = arrayListOf<() -> Unit>()

        var initialized: Boolean = false
            private set

        @JvmStatic var instance: MyApplication? = null
            private set

        fun registerInitializedCallback(callback: () -> Unit) {
            if (initialized) {
                callback()
            } else {
                waitList.add(callback)
            }
        }
    }
}
