package com.youseokhwan.commitmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.youseokhwan.commitmanager.ui.githubid.GitHubIdFragment
import com.youseokhwan.commitmanager.ui.welcome.WelcomeFragment

/**
 * FirstRunActivity
 *
 * 최초 실행 시 초기 설정 Activity
 */
class FirstRunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run)

        // WelcomeFragment로 전환
        onFragmentChange("welcome")

        // 초기 설정을 모두 마치면 isFirstRun을 false로 변경

        // MainActivity로 이동
//        startActivity<MainActivity>()
//        finish()
    }

    /**
     * Fragment를 전환하는 함수
     * @param name 전환할 Fragment의 이름
     */
    fun onFragmentChange(name: String) {
        when (name) {
            // WelcomeFragment로 전환
            "welcome" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.framelayout_firstrun, WelcomeFragment()).commit()
            }
            // GitHubIdFragment로 전환
            "githubid" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.framelayout_firstrun, GitHubIdFragment()).commit()
            }
            // 유효한 Fragment 이름이 아닐 경우
            else -> {
                TODO()
            }
        }
    }
}
