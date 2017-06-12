package com.weichen2046.filesender2.ui

import android.content.Intent
import android.os.Bundle
import com.weichen2046.filesender2.MyApplication

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.registerInitializedCallback {
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }
}
