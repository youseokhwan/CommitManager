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
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.Commit
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.image
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
//        toast("${name}님 환영합니다!")

        // Toolbar Title 설정
        MainActivity_Toolbar.title = name

        // Toolbar 클릭하면 UserInfo 패널 Visible
        MainActivity_Toolbar.setOnClickListener {
            if (MainActivity_ConstraintLayout_UserInfo.visibility == View.INVISIBLE) {
                MainActivity_ConstraintLayout_UserInfo.startAnimation(fadeIn)
                MainActivity_ConstraintLayout_UserInfo.visibility = View.VISIBLE
            }
        }

        // UserInfo 패널 설정
        Glide.with(this).load(imgSrc).into(MainActivity_ImageView_Avatar)
        MainActivity_TextView_GitHubId.text = id
        MainActivity_TextView_Follower.text = "follower: ${follower}명"
        MainActivity_TextView_Following.text = "following: ${following}명"

        // 앱 실행 시 오늘 날짜로 초기화

        // 오늘 커밋 여부 판단하여 로고 변경
        // GET("/userinfo?id=${id}")
        UserRetrofit.getService().getTodayCommit(id = id, token = "defaultToken")
            .enqueue(object : Callback<Commit> {
                override fun onFailure(call: Call<Commit>?, t: Throwable?) {
                    toast("오류가 발생했습니다. 다시 시도해주세요.")
                    throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                }

                override fun onResponse(call: Call<Commit>, response: Response<Commit>) {
                    if (response.isSuccessful) {
                        Log.d("CommitManagerLog", response.body().toString())

                        // count가 0보다 크면 커밋이 완료된 것임
                        if (response.body()?.count?:0 > 0) {
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_check_black_24dp)
                        } else {
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_close_black_24dp)
                        }
                    } else {
                        toast("오류가 발생했습니다. 다시 시도해주세요.")
                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
                    }
                }
            })
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
                 * 이 메소드에 의해 Invisible 처리된 후 Toolbar OnClick 이벤트에 의해 다시 Visible 처리됨
                 * Toolbar의 OnClick 이벤트가 발생하지 않도록 true 리턴
                 */
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
