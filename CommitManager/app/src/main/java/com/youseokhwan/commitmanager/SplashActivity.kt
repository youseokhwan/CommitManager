package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.jetbrains.anko.startActivity

/**
 * SplashActivity
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // 최초 실행일 경우(초기 설정이 진행되지 않았을 경우) FirstRunActivity로 이동하여 초기 설정 진행
        // 최초 실행이 아닐 경우 MainActivity로 이동
        if (settings.getBoolean("isFirstRun", true)) {
            Log.d("CommitManagerLog", "isFirstRun: true")

            // FirstRunActivity로 이동
            startActivity<FirstRunActivity>()
            finish()
        } else {
            Log.d("CommitManagerLog", "isFirstRun: false")

            // MainActivity로 이동
            startActivity<MainActivity>()
            // =====================================================================================
//            startActivity<FirstRunActivity>()
            // =====================================================================================
            finish()
        }
    }
}
