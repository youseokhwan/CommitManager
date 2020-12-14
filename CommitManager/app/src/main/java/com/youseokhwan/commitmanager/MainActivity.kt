package com.youseokhwan.commitmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
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

    // 뒤로가기 2번 누르면 앱 종료
    private val finishIntervalTime: Long = 3000
    private var backPressedTime   : Long = 0

    // 애니메이션 변수 선언
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

        // Toolbar Title 설정
        toolbar.title = SplashActivity.name

        // Toolbar 클릭하면 UserInfo 패널 Visible
        toolbar.setOnClickListener {
            if (cstLyUserInfo.visibility == View.INVISIBLE) {
                cstLyUserInfo.startAnimation(fadeIn)
                cstLyUserInfo.visibility = View.VISIBLE
                toolbar.title = getString(R.string.app_name)
            }
        }

        // UserInfo 패널 설정
        Glide.with(this)
            .load(SplashActivity.imgSrc)
            .into(ImgAvatar)
        txtGitHubId .text = SplashActivity.id
        txtFollower .text = "follower: ${SplashActivity.follower}명"
        txtFollowing.text = "following: ${SplashActivity.following}명"

        // GitHub Page 버튼 클릭하면 Intent 호출
        btnGitHubPage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${SplashActivity.id}/"))
            startActivity(intent)
        }

        // 최초 실행이면 AlarmManager 실행
        if (SplashActivity.isFirstRun) {
            startAlarmManager()

            // isFirstRun false로 변경하고 SharedPreferences에 적용
            val settings = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            with(settings.edit()) {
                SplashActivity.isFirstRun = false

                putBoolean("isFirstRun" , SplashActivity.isFirstRun)
                apply()
            }
        }
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
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // UserInfo 패널이 Visible 상태일 때
        if (cstLyUserInfo.visibility == View.VISIBLE) {
            val rect = Rect()
            cstLyUserInfo.getGlobalVisibleRect(rect)

            // Touch Point가 UserInfo 패널 범위 바깥이라면 Invisible
            if (!rect.contains(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)) {
                cstLyUserInfo.startAnimation(fadeOut)
                cstLyUserInfo.visibility = View.INVISIBLE
                toolbar.title = SplashActivity.id

                /*
                 * UserInfo 패널이 Visible인 상태에서 Toolbar를 터치하면
                 * 이 메소드에 의해 Invisible 처리된 후 Toolbar의 onClickEvent에 의해 다시 Visible 처리됨
                 * Toolbar의 OnClick 이벤트가 발생하지 않도록 true를 리턴
                 */
                return true
            }
        }

        return super.dispatchTouchEvent(event)
    }

    /**
     * AlarmManager 시작
     */
    private fun startAlarmManager() {
        Log.d("CommitManagerLog", "startAlarmManager() 호출됨")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(applicationContext, 0, it, 0)
        }
        val bootReceiver = ComponentName(applicationContext, DeviceBootReceiver::class.java)

        // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
        if (SplashActivity.alarmOption != AlarmOption.NONE.value) {
            val hour = SplashActivity.alarmTime.substring(0..1).toInt()
            val min  = SplashActivity.alarmTime.substring(3..4).toInt()

            // 알림 시간 설정
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE     , min )
                set(Calendar.SECOND     , 0   )
            }

            // 알림 시작
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, // 매일 반복
                alarmIntent
            )

            // 디바이스 재시작 대응
            applicationContext.packageManager.setComponentEnabledSetting(
                bootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            Toast.makeText(applicationContext, "매일 ${hour}시 ${min}분에 커밋 여부를 알려드려요", Toast.LENGTH_LONG).show()
        } else {
            // AlarmOption이 NONE인 경우 반복 작업 취소
            alarmManager.cancel(alarmIntent)

            // 디바이스 재시작 대응 취소
            applicationContext.packageManager.setComponentEnabledSetting(
                bootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}
