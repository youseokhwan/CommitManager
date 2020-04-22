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

    companion object {
        var isFirstRun = true       // 최초 실행 여부
        var id         = ""         // User ID
        var token      = ""         // OAuth Token
        var first      = ""         // 1차 알람 시간
        var second     = ""         // 2차 알람 시간
        var name       = ""         // Username
        var imgSrc     = ""         // Avartar 이미지 경로
        var follower   = 0          // follower 수
        var following  = 0          // following 수
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // 최초 실행일 경우(초기 설정이 진행되지 않았을 경우) FirstRunActivity로 이동하여 초기 설정 진행
        isFirstRun = settings.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            Log.d("CommitManagerLog", "isFirstRun: true")

            // FirstRunActivity로 이동
            startActivity<FirstRunActivity>()
            finish()
        } else {
            Log.d("CommitManagerLog", "isFirstRun: false")

            // =====================================================================================
            // 앱을 사용하는 중간에 GitHub OAuth를 Revoke 할 경우를 대비하여
            // 이 부분에서 API를 통해 OAuth 호출을 고려해야 함
            // =====================================================================================

            // SharedPreferences에 등록된 데이터를 Companion Object 변수들에 대입
            // id = ""
            // token = ""

            // MainActivity로 이동
//            startActivity<MainActivity>()
            // =====================================================================================
            // 테스트 - 앱 실행할 때마다 초기 설정을 진행하도록 설정 (개발 완료 후 삭제)
            startActivity<FirstRunActivity>()
            // =====================================================================================
            finish()
        }
    }
}
