package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * SplashActivity
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App의 설정값을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // 최초 실행일 경우(초기 설정이 진행되지 않았을 경우) FirstRunActivity로 이동하여 초기 설정 진행
        // 최초 실행이 아닐 경우 MainActivity로 이동
        if (settings.getBoolean("isFirstRun", true)) {
            Log.d("CommitManagerLog", "isFirstRun: true")

            // =====================================================================================
            /*
             * 우선 임시로 실행만 시켜도 초기 설정을 완료한 것으로 간주함
             * 초기 설정을 마치면 isFirstRun을 false로 변경하도록 구현할 것
             * 초기 설정 Fragment가 모두 구현되면 FirstRunActivity 최하단으로 옮길 것
             */
            val editor: SharedPreferences.Editor = settings.edit()
            editor.putBoolean("isFirstRun", false)
            editor.apply()
            // =====================================================================================

            // FirstRunActivity로 이동
            startActivity<FirstRunActivity>()
            finish()
        } else {
            Log.d("CommitManagerLog", "isFirstRun: false")

            // MainActivity로 이동
            startActivity<MainActivity>()
            finish()
        }
    }
}
