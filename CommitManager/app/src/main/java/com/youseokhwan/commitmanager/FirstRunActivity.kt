package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.ui.initial.InitialFragment
import com.youseokhwan.commitmanager.ui.welcome.WelcomeFragment
import kotlinx.android.synthetic.main.fragment_initial.*
import org.jetbrains.anko.startActivity

/**
 * 최초 실행 시 초기 설정을 진행하는 Activity
 */
class FirstRunActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        // WelcomeFragment로 전환
        onFragmentChange("welcome")
    }

    /**
     * Fragment를 전환하는 메소드
     * @param name 전환할 Fragment의 이름
     */
    fun onFragmentChange(name: String) {
        when (name) {
            // WelcomeFragment로 전환
            "welcome" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.FirstRunActivity_FrameLayout, WelcomeFragment()).commit()
            }
            // InitialFragment로 전환
            "initial" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.FirstRunActivity_FrameLayout, InitialFragment()).commit()
            }
            // 유효한 Fragment 이름이 아닐 경우
            else -> {
                throw InvalidParameterNameException(
                    "InvalidParameterNameException:" +
                            "${name}은 유효하지 않은 Fragment 이름입니다."
                )
            }
        }
    }

    /**
     * 초기 설정을 저장하고 MainActivity로 전환하는 메소드
     */
    fun finishInitialSettings() {
        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()

        // 설정 값을 settings에 저장
        Log.d("CommitManagerLog", "GitHub ID: ${InitialFragment_EditText_GithubId.text}, " +
                "First: ${InitialFragment_EditText_First.text}, " +
                "Second: ${InitialFragment_CheckBox_Second.isChecked}, " +
                "${InitialFragment_EditText_Second.text}")

        // 위의 값들을 settings에 넣는 코드 구현

        editor.putBoolean("isFirstRun", false)
        editor.apply()

        // MainActivity로 이동
        startActivity<MainActivity>()
        finish()
    }
}
