package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        /*
         * 최초 실행일 경우 FirstRunActivity, 아닐 경우 MainActivity로 이동
         */
        if (settings.getBoolean("isFirstRun", true)) {
            /*
             * editor 코드 블럭은 FirstRunActivity에서 구현해야 함
             * 기본 설정을 마치면 isFirstRun을 false로 변경하도록 구현하고 line 25-28은 삭제할 것
             */
            val editor: SharedPreferences.Editor = settings.edit()

            editor.putBoolean("isFirstRun", false)
            editor.apply()

            Log.d("CommitManagerLog", "First Run")

            startActivity<FirstRunActivity>()
            finish()
        } else {
            startActivity<MainActivity>()
            finish()
        }
    }
}
