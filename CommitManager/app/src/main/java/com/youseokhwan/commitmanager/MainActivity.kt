package com.youseokhwan.commitmanager

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

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
                 * Toolbar의 OnClick 이벤트가 발생하지 않도록 true를 리턴하는 것임
                 */
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
