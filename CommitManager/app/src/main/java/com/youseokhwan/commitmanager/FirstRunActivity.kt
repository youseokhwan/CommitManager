package com.youseokhwan.commitmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.retrofit.UserInfo
import com.youseokhwan.commitmanager.fragment.InitialFragment
import com.youseokhwan.commitmanager.fragment.WelcomeFragment
import com.youseokhwan.commitmanager.realm.Commit
import com.youseokhwan.commitmanager.realm.Setting
import com.youseokhwan.commitmanager.realm.User
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_initial.*

/**
 * 최초 실행 시 초기 설정을 진행하는 Activity
 * @property finishIntervalTime
 * @property backPressedTime
 */
class FirstRunActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    // 뒤로가기 2번 누르면 앱 종료
    private val finishIntervalTime: Long = 3000
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstrun)

        // Realm 인스턴스 초기화
        realm = Realm.getDefaultInstance()

        // WelcomeFragment로 전환
        onFragmentChange("welcome")
    }

    /**
     * 뒤로가기 2번 누르면 앱 종료
     */
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..finishIntervalTime) {
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
        realm.beginTransaction()

        // User 데이터 수정
        val userItem = realm.where<User>().findFirst()
        userItem?.name      = userInfo?.name.toString()
        userItem?.imgSrc    = userInfo?.imgSrc.toString()
        userItem?.follower  = userInfo?.follower ?: 0
        userItem?.following = userInfo?.following ?: 0

        // Setting 데이터 삽입
        val settingItem = realm.createObject<Setting>()
        settingItem.isFirstRun = true
        when (rgNotification.checkedRadioButtonId) {
            R.id.rbNoti01 -> settingItem.alarmOption = 0  // 알람 받지 않기
            R.id.rbNoti02 -> settingItem.alarmOption = 1  // 커밋 안한 날만 받기
            R.id.rbNoti03 -> settingItem.alarmOption = 2  // 커밋한 날도 알림 받기
        }
        settingItem.alarmTime = edtTime.text.toString()
        when (rgVibrate.checkedRadioButtonId) {
            R.id.rbVib01 -> settingItem.vibOption = 0  // 진동
            R.id.rbVib02 -> settingItem.vibOption = 1  // 무음
        }

        // Commit 데이터 삽입
        val commitItem = realm.createObject<Commit>()
        commitItem.thisSun = false
        commitItem.thisMon = false
        commitItem.thisTue = false
        commitItem.thisWed = false
        commitItem.thisThu = false
        commitItem.thisFri = false
        commitItem.thisSat = false
        commitItem.committingStraight = 0

        // =========================================================================================
        // 추후 Achievement 데이터 삽입
        // =========================================================================================

        realm.commitTransaction()

        // MainActivity로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
