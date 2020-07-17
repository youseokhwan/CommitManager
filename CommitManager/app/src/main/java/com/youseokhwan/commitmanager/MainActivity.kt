package com.youseokhwan.commitmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.youseokhwan.commitmanager.alarm.AlarmOption
import com.youseokhwan.commitmanager.alarm.AlarmReceiver
import com.youseokhwan.commitmanager.alarm.DeviceBootReceiver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

/**
 * MainActivity
 * @property finishIntervalTime
 * @property backPressedTime
 * @property fadeIn
 * @property fadeOut
 */
class MainActivity : AppCompatActivity() {

//    private var id: String = "DefaultID"

    // 뒤로가기 2번 누르면 앱 종료
    private val finishIntervalTime: Long = 3000
    private var backPressedTime   : Long = 0

    // 에니메이션 변수 선언
    private lateinit var fadeIn : Animation
    private lateinit var fadeOut: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 애니메이션 변수 초기화
        fadeIn  = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // NavController 설정
        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        // AlarmManager 실행
        startAlarmManager()

        // Toolbar Title 설정
        toolbar.title = SplashActivity.name

        // Toolbar 클릭하면 UserInfo 패널 Visible
        toolbar.setOnClickListener {
            if (cstLyUserInfo.visibility == View.INVISIBLE) {
                cstLyUserInfo.startAnimation(fadeIn)
                cstLyUserInfo.visibility = View.VISIBLE
            }
        }

        // UserInfo 패널 설정
        Glide.with(this).load(SplashActivity.imgSrc).into(ImgAvatar)
        txtGitHubId .text = SplashActivity.id
        txtFollower .text = "follower: ${SplashActivity.follower}명"
        txtFollowing.text = "following: ${SplashActivity.following}명"

        // =========================================================================================
        // 테스트 코드
        Log.d("CommitManagerLog", "alarmOption: ${SplashActivity.alarmOption}")
        Toast.makeText(applicationContext, "(테스트) 설정된 alarmTime: ${SplashActivity.alarmTime}"
            , Toast.LENGTH_SHORT).show()
        // =========================================================================================
    }

    /**
     * 뒤로가기 2번 누르면 앱 종료
     */
    override fun onBackPressed() {
        val tempTime     = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..finishIntervalTime) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "종료하려면 한번 더 눌러주세요", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * UserInfo 패널 바깥 영역을 터치하면 Invisible 처리
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // UserInfo 패널이 Visible 상태일 때
        if (cstLyUserInfo.visibility == View.VISIBLE) {
            val rect = Rect()
            cstLyUserInfo.getGlobalVisibleRect(rect)

            // Touch Point가 UserInfo 패널 범위 바깥이라면 Invisible
            if (!rect.contains(ev?.x?.toInt() ?: 0, ev?.y?.toInt() ?: 0)) {
                cstLyUserInfo.startAnimation(fadeOut)
                cstLyUserInfo.visibility = View.INVISIBLE

                /*
                 * UserInfo 패널이 Visible인 상태에서 Toolbar를 터치하면
                 * 이 메소드에 의해 Invisible 처리된 후 Toolbar의 onClickEvent에 의해 다시 Visible 처리됨
                 * Toolbar의 OnClick 이벤트가 발생하지 않도록 true를 리턴
                 */
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * AlarmManager 시작
     */
    private fun startAlarmManager() {
//        Log.d("CommitManagerLog", "startAlarmManager() 진입")

        // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
        if (SplashActivity.alarmOption != AlarmOption.NONE.value) {
            val packageManager = this.packageManager
            val receiver = ComponentName(this, DeviceBootReceiver::class.java)
            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // String 타입인 SplashActivity.alarmTime 값을 Calendar 타입으로 변환
            val time = Calendar.getInstance()
            time.timeInMillis = System.currentTimeMillis()
            time.set(Calendar.HOUR_OF_DAY, SplashActivity.alarmTime.substring(0..1).toInt())
            time.set(Calendar.MINUTE, SplashActivity.alarmTime.substring(3..4).toInt())
            time.set(Calendar.SECOND, 0)

            // 설정한 시간이 현재 시간을 지났으면 DAY + 1
            if (time.before(Calendar.getInstance())) {
                time.add(Calendar.DATE, 1)
            }

//            Log.d("CommitManagerLog", "time: $time")

            // 반복 설정
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, time.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)

            // 부팅 후 Receiver 사용가능하도록 설정
            packageManager.setComponentEnabledSetting(
                receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        }
    }

    /**
     * Settings Fragment의 적용하기 버튼 클릭 시 변경된 설정 저장
     */
    fun updateSettings() {
        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()

        // 설정 값을 Companion Object에 저장
        when (rgSetNotification.checkedRadioButtonId) {
            R.id.rbSetNoti01 -> SplashActivity.alarmOption = 0  // 알람 받지 않기
            R.id.rbSetNoti02 -> SplashActivity.alarmOption = 1  // 커밋 안한 날만 받기
            R.id.rbSetNoti03 -> SplashActivity.alarmOption = 2  // 커밋한 날도 알림 받기
        }
        SplashActivity.alarmTime   = edtSetTime.text.toString()
        when (rgSetVibrate.checkedRadioButtonId) {
            R.id.rbSetVib01 -> SplashActivity.vibOption = 0  // 진동
            R.id.rbSetVib02 -> SplashActivity.vibOption = 1  // 무음
        }

        // 설정 값을 settings에 저장
        editor.putInt    ("alarmOption", SplashActivity.alarmOption)
        editor.putString ("alarmTime"  , SplashActivity.alarmTime)
        editor.putInt    ("vibOption"  , SplashActivity.vibOption)

        editor.apply()
    }
}
