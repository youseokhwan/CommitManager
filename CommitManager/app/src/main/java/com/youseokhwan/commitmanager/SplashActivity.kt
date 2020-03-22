package com.youseokhwan.commitmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity<MainActivity>()
        finish()
    }
}
