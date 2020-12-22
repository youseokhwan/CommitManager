package com.youseokhwan.commitmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import com.youseokhwan.commitmanager.realm.Setting
import com.youseokhwan.commitmanager.realm.User
import io.realm.Realm
import io.realm.kotlin.where
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

    private lateinit var realm: Realm

    // 뒤로가기 2번 누르면 앱 종료
    private val finishIntervalTime: Long = 3000
    private var backPressedTime   : Long = 0

    // 애니메이션 변수 선언
    private lateinit var fadeIn : Animation
    private lateinit var fadeOut: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Realm 인스턴스 초기화
        realm = Realm.getDefaultInstance()
        val userItem = realm.where<User>().findFirst()
        val settingItem = realm.where<Setting>().findFirst()

        // 애니메이션 변수 초기화
        fadeIn  = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // NavController 설정
        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        // Toolbar Title 설정
//        toolbar.title = SplashActivity.name
        toolbar.title = userItem?.name ?: "1일 1커밋"

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
            .load(userItem?.imgSrc)
            .into(ImgAvatar)
        txtGitHubId .text = userItem?.id ?: "error"
        txtFollower .text = "follower: ${userItem?.follower ?: -1}명"
        txtFollowing.text = "following: ${userItem?.following ?: -1}명"

        // GitHub Page 버튼 클릭하면 Intent 호출
        btnGitHubPage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${userItem?.id ?: ""}/"))
            startActivity(intent)
        }

        // 최초 실행이면 AlarmManager 실행하고 isFirstRun을 false로 수정
        if (settingItem?.isFirstRun != false) {
            startAlarmManager()

            // isFirstRun을 false로 설정
            realm.beginTransaction()
            settingItem?.isFirstRun = false
            realm.commitTransaction()
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
        val userItem = realm.where<User>().findFirst()

        // UserInfo 패널이 Visible 상태일 때
        if (cstLyUserInfo.visibility == View.VISIBLE) {
            val rect = Rect()
            cstLyUserInfo.getGlobalVisibleRect(rect)

            // Touch Point가 UserInfo 패널 범위 바깥이라면 Invisible
            if (!rect.contains(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)) {
                cstLyUserInfo.startAnimation(fadeOut)
                cstLyUserInfo.visibility = View.INVISIBLE
                toolbar.title = userItem?.name ?: "1일 1커밋"

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

        val settingItem = realm.where<Setting>().findFirst()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(applicationContext, 0, it, 0)
        }
        val bootReceiver = ComponentName(applicationContext, DeviceBootReceiver::class.java)

        // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
        if (settingItem?.alarmOption != AlarmOption.NONE.value) {
            val hour = settingItem?.alarmTime?.substring(0..1)?.toInt()
            val min  = settingItem?.alarmTime?.substring(3..4)?.toInt()

            // 알림 시간 설정
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour ?: 22)
                set(Calendar.MINUTE     , min  ?: 0 )
                set(Calendar.SECOND     , 0         )
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
