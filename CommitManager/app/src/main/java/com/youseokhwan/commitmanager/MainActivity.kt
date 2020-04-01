package com.youseokhwan.commitmanager

import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
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
 */
class MainActivity : AppCompatActivity() {

    // 뒤로가기 2번 누르면 앱 종료
    private val FINISH_INTERVAL_TIME: Long = 3000
    private var backPressedTime     : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // NavController 설정
        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        // 사용자 설정을 저장하는 SharedPreferences
        val settings: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val name: String = settings.getString("name", "null")!!
        val imgSrc: String = settings.getString("imgSrc", "null")!!

        // 환영 Toast 메시지 출력
        toast("${name}님 환영합니다!")

        // ActionBar 설정
        supportActionBar?.title = name

        // =========================================================================================
        // ActionBar 로고 설정

        // 1. imgSrc를 Bitmap으로 변환한다.
        // 2. Bitmap을 Drawable로 변환한다.
        // 3. setLogo(Drawable)

        // --- OR ---

        // 1. Glide 라이브러리 import
        // 2. Drawable 변수 선언
//        lateinit var logo: Drawable
        // 3. Glide로 Drawable 초기화
//        Glide.with(this).load(imgSrc).into()
        // 4. setLogo(Drawable)

        // --- 이것도 안되면... ---

        // 1. Glide 라이브러리 import
        // 2. ImageView 변수 선언
//        val tempImageView: ImageView = ImageView(this)
        // 3. Glide로 ImageView 초기화
//        Glide.with(this).load(imgSrc).into(tempImageView)
        // 4.
//        val tempBitmapDrawable: BitmapDrawable = tempImageView.drawable
//        val drawable: Drawable = tempImageView.drawable
        // 5. setLogo(Drawable)
//        supportActionBar?.setLogo(drawable)

//        supportActionBar?.setLogo()
//        supportActionBar?.setIcon() // 둘 중 하나 사용

        // 런타임 에러 해결해야 함
        // =========================================================================================
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
}
