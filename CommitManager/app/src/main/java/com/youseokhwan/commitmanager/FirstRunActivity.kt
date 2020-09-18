package com.youseokhwan.commitmanager

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.retrofit.UserInfo
import com.youseokhwan.commitmanager.ui.firstrun.InitialFragment
import com.youseokhwan.commitmanager.ui.firstrun.WelcomeFragment
import kotlinx.android.synthetic.main.fragment_initial.*

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
            Toast.makeText(applicationContext, "종료하려면 한번 더 눌러주세요", Toast.LENGTH_SHORT).show()
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
                    .replace(R.id.frLy, WelcomeFragment()).commit()
            }
            // InitialFragment로 전환
            "initial" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frLy, InitialFragment()).commit()
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

        // 설정 값을 Companion Object에 저장
        SplashActivity.id          = edtGithubId.text.toString()
        when (rgNotification.checkedRadioButtonId) {
            R.id.rbNoti01 -> SplashActivity.alarmOption = 0  // 알람 받지 않기
            R.id.rbNoti02 -> SplashActivity.alarmOption = 1  // 커밋 안한 날만 받기
            R.id.rbNoti03 -> SplashActivity.alarmOption = 2  // 커밋한 날도 알림 받기
        }
        SplashActivity.alarmTime   = edtTime    .text.toString()
        when (rgVibrate.checkedRadioButtonId) {
            R.id.rbVib01 -> SplashActivity.vibOption = 0  // 진동
            R.id.rbVib02 -> SplashActivity.vibOption = 1  // 무음
        }

        // 설정 값을 settings에 저장
        editor.putString ("id"         , SplashActivity.id)
        editor.putInt    ("alarmOption", SplashActivity.alarmOption)
        editor.putString ("alarmTime"  , SplashActivity.alarmTime)
        editor.putInt    ("vibOption"  , SplashActivity.vibOption)
        editor.putBoolean("isFirstRun" , SplashActivity.isFirstRun)

        // response를 Companion Object에 저장
        SplashActivity.name      = userInfo?.name  .toString()
        SplashActivity.imgSrc    = userInfo?.imgSrc.toString()
        SplashActivity.follower  = userInfo?.follower ?:0
        SplashActivity.following = userInfo?.following?:0

        // response를 settings에 저장
        editor.putString("name"     , SplashActivity.name)
        editor.putString("imgSrc"   , SplashActivity.imgSrc)
        editor.putInt   ("follower" , SplashActivity.follower)
        editor.putInt   ("following", SplashActivity.following)

        editor.apply()

        // MainActivity로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
