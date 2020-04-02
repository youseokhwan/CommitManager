package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

/**
 * MainActivity
 * @property FINISH_INTERVAL_TIME
 * @property backPressedTime
 * @property fadeIn
 * @property fadeOut
 */
class MainActivity : AppCompatActivity() {

    // 뒤로가기 2번 누르면 앱 종료
    private val FINISH_INTERVAL_TIME: Long = 3000
    private var backPressedTime     : Long = 0

    // 에니메이션 변수 선언
    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 애니메이션 변수 초기화
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // NavController 설정
        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val id: String = settings.getString("id", "null")!!
        val name: String = settings.getString("name", "null")!!
        val imgSrc: String = settings.getString("imgSrc", "null")!!
        val follower: Int = settings.getInt("follower", 0)
        val following: Int = settings.getInt("following", 0)

        // 환영 Toast 메시지 출력
        toast("${name}님 환영합니다!")

        // Toolbar 설정
        MainActivity_Toolbar.title = name
        MainActivity_Toolbar.setOnClickListener {
            if (MainActivity_ConstraintLayout_UserInfo.visibility == View.INVISIBLE) {
                MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeIn)
                MainActivity_ConstraintLayout_UserInfo.visibility = View.VISIBLE
            } else {
                MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeOut)
                MainActivity_ConstraintLayout_UserInfo.visibility = View.INVISIBLE
            }
        }

        // UserInfo 패널 설정
        Glide.with(this).load(imgSrc).into(MainActivity_ImageView_Avatar)
        MainActivity_TextView_GitHubId.text = id
        MainActivity_TextView_Follower.setText("follower: ${follower}명")
        MainActivity_TextView_Following.setText("following: ${following}명")
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
            toast("한번 더 누르면 종료합니다.")
        }
    }

    // =============================================================================================
    // UserInfo의 바깥 영역을 터치하면 Invisible 처리하려는 의도
    // 의도대로 안되는 중

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        // UserInfo 패널이 보일 때만 실행
//        if (MainActivity_ConstraintLayout_UserInfo.visibility == View.INVISIBLE) {
//            return true
//        }
//
//        val viewRect: Rect = Rect()
//        MainActivity_ConstraintLayout_UserInfo.getGlobalVisibleRect(viewRect)
//
//        // UserInfo의 바깥 영역을 터치하면 Invisible 처리
//        return if (!viewRect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
//            MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeOut)
//            MainActivity_ConstraintLayout_UserInfo.visibility = View.INVISIBLE
//            true
//        } else {
//            super.dispatchTouchEvent(ev)
//        }
//    }
    // =============================================================================================
}
