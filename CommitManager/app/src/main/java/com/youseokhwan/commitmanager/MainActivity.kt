package com.youseokhwan.commitmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
        MainActivity_Toolbar.title = SplashActivity.name

        // Toolbar 클릭하면 UserInfo 패널 Visible
        MainActivity_Toolbar.setOnClickListener {
            if (MainActivity_ConstraintLayout_UserInfo.visibility == View.INVISIBLE) {
                MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeIn)
                MainActivity_ConstraintLayout_UserInfo.visibility = View.VISIBLE
            }
        }

        // UserInfo 패널 설정
        Glide.with(this).load(SplashActivity.imgSrc).into(MainActivity_ImageView_Avatar)
        MainActivity_TextView_GitHubId .text = SplashActivity.id
        MainActivity_TextView_Follower .text = "follower: ${SplashActivity.follower}명"
        MainActivity_TextView_Following.text = "following: ${SplashActivity.following}명"

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
        if (MainActivity_ConstraintLayout_UserInfo.visibility == View.VISIBLE) {
            val rect = Rect()
            MainActivity_ConstraintLayout_UserInfo.getGlobalVisibleRect(rect)

            // Touch Point가 UserInfo 패널 범위 바깥이라면 Invisible
            if (!rect.contains(ev?.x?.toInt() ?: 0, ev?.y?.toInt() ?: 0)) {
                MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeOut)
                MainActivity_ConstraintLayout_UserInfo.visibility = View.INVISIBLE

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
        // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
        if (SplashActivity.alarmOption != AlarmOption.NONE.value) {
            val packageManager = this.packageManager
            val receiver = ComponentName(this, DeviceBootReceiver::class.java)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(applicationContext,
                0, Intent(applicationContext, AlarmReceiver::class.java), 0)

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
     * AlarmManager 종료
     * 설정이 변경되면 stop 후 start
     * (만약 로직상 필요없다면 삭제)
     */
    private fun stopAlarmManager() {

    }
}
