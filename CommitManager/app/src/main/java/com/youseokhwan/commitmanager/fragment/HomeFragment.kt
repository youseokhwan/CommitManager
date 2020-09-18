package com.youseokhwan.commitmanager.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.Commit
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * 오늘 커밋이 있는지 알려주는 이미지와 이번 주 커밋 통계를 표시하는 메인 Fragment
 */
class HomeFragment : Fragment() {

    // 에니메이션 변수 선언
    private lateinit var fadeIn : Animation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // 애니메이션 변수 초기화
        fadeIn  = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        // 오늘 커밋 여부 아이콘 업데이트
        // GET("/commit?id=${id}&token=${token}")
        UserRetrofit.getService().getTodayCommit(id = SplashActivity.id, token = SplashActivity.token)
            .enqueue(object : Callback<Commit> {
                override fun onFailure(call: Call<Commit>?, t: Throwable?) {
                    Toast.makeText(context, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                }

                override fun onResponse(call: Call<Commit>, response: Response<Commit>) {
                    if (response.isSuccessful) {
                        // =========================================================================
                        Log.d("CommitManagerLog", "id = ${SplashActivity.id}, token = ${SplashActivity.token}")
                        Log.d("CommitManagerLog", "Response: ")
                        Log.d("CommitManagerLog", response.body().toString())
                        // =========================================================================

                        // count가 0보다 크면 커밋이 완료된 것임
                        if (response.body()?.count?:"0" != "0") {
                            // 커밋 내역이 있을 경우 V 이미지로 변경
                            imgDaily.setImageResource(R.drawable.ic_check_black_24dp)
                            imgDaily.startAnimation(fadeIn)

                            // =====================================================================
                            Log.d("CommitManagerLog", "repository = ${response.body()?.repository}")
                            Log.d("CommitManagerLog", "msg = ${response.body()?.msg}")
                            // =====================================================================

                            // 커밋 내역이 있을 경우 Repository 초기화
                            txtRepository.text = response.body()?.repository.toString()
                            txtRepository.visibility = View.VISIBLE

                            // 커밋 내역이 있고 커밋 메시지도 있을 경우 Msg 초기화
                            if (txtMsg.text != null && txtMsg.text != "") {
                                txtMsg.text = response.body()?.msg.toString()
                                txtMsg.visibility = View.VISIBLE
                            }

                            // =====================================================================
                            // 커밋 내역과 메시지를 띄울 때 애니메이션 추가해야 함
                            // =====================================================================
                        } else {
                            // 커밋 내역이 없을 경우 X 이미지로 변경
                            imgDaily.setImageResource(R.drawable.ic_close_black_24dp)
                            imgDaily.startAnimation(fadeIn)

                            // 커밋 내역이 없을 경우 Repository, Msg 숨기기
                            txtRepository.visibility = View.GONE
                            txtMsg       .visibility = View.GONE

                            // =====================================================================
                            // 커밋 내역과 메시지를 지울 때 애니메이션 추가해야 함
                            // =====================================================================
                        }
                    } else {
                        Toast.makeText(context, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
                    }
                }
            })

        return root
    }

    override fun onStart() {
        // 오늘 날짜로 초기화
        setTodayDate()
        super.onStart()
    }

    /**
     * 화면 상단 TextView를 오늘 날짜로 초기화
     */
    private fun setTodayDate() {
        val dayOfWeekKorean = arrayOf("", "일", "월", "화", "수", "목", "금", "토")

        val cal = Calendar.getInstance()
        var today = cal.get(Calendar.YEAR).toString() + "년 "
        today += (cal.get(Calendar.MONTH) + 1).toString() + "월 "
        today += cal.get(Calendar.DATE).toString() + "일 ("
        today += dayOfWeekKorean[cal.get(Calendar.DAY_OF_WEEK)] + ")"

        txtToday.text = today
    }
}