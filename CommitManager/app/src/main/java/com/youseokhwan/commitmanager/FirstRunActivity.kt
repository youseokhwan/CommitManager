package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.retrofit.UserInfo
import com.youseokhwan.commitmanager.ui.firstrun.InitialFragment
import com.youseokhwan.commitmanager.ui.firstrun.WelcomeFragment
import kotlinx.android.synthetic.main.fragment_initial.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 최초 실행 시 초기 설정을 진행하는 Activity
 * @property FINISH_INTERVAL_TIME
 * @property backPressedTime
 */
class FirstRunActivity : AppCompatActivity() {

    // 뒤로가기 2번 누르면 앱 종료
    private val FINISH_INTERVAL_TIME: Long = 3000
    private var backPressedTime     : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        // WelcomeFragment로 전환
        onFragmentChange("welcome")
    }

    /**
     * 뒤로가기 2번 누르면 앱 종료
     */
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            toast("종료하려면 한번 더 눌러주세요")
        }
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
                    .replace(R.id.FirstRunActivity_FrameLayout,
                        WelcomeFragment()
                    ).commit()
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
     * @param userInfo
     */
    fun finishInitialSettings(userInfo: UserInfo?) {
        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()

//        Log.d("CommitManagerLog", "GitHub ID: ${InitialFragment_EditText_GithubId.text}, "
//                + "First: ${InitialFragment_EditText_First.text}, "
//                + "Second: ${InitialFragment_CheckBox_Second.isChecked}, "
//                + "${InitialFragment_EditText_Second.text}")

        // 설정 값을 settings에 저장
        editor.putString("id", InitialFragment_EditText_GithubId.text.toString())
        editor.putString("first", InitialFragment_EditText_First.text.toString())
        editor.putString("second", InitialFragment_EditText_Second.text.toString())
        editor.putBoolean("isFirstRun", false)

        // =========================================================================================
        // UserInfo GET 호출로 받은 response를 settings에 저장
        editor.putString("name", userInfo?.name.toString())
        editor.putString("imgSrc", userInfo?.imgSrc.toString())
        editor.putInt("follower", userInfo?.follower?:0)
        editor.putInt("following", userInfo?.following?:0)

        // 앱 실행중에 유저 정보가 바뀌는 경우 대처해야 함
        // onCreate 때마다 updateUserInfo() 형식으로 갱신
        // 혹은 상단 Action Bar를 터치할 때 유저 정보를 보여주고 확장하는 방식
        // =========================================================================================

        editor.apply()

        // MainActivity로 이동
        startActivity<MainActivity>()
        finish()
    }
}
