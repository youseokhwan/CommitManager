package com.youseokhwan.commitmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.startActivity

class FirstRunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run)

        /*
         * 최초 실행 시 환영 문구 출력
         * GitHub ID 입력받고 알람 시간 설정 등 작업 후 MainActivity로 이동
         * 실행 시 isFirstRun 값을 바로 false로 바꾸는 것이 아닌 기본 설정이 끝날 때 바꿀 것
         */

//        startActivity<MainActivity>()
//        finish()
    }
}
