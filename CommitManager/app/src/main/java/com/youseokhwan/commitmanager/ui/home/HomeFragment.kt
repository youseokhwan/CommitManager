package com.youseokhwan.commitmanager.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
 * @property homeViewModel
 */
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

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
                            // 변경 애니메이션 추가할 것
                            // 커밋 내역이 있을 경우 V 이미지로 변경
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_check_black_24dp)

                            // =====================================================================
                            Log.d("CommitManagerLog", "repository = ${response.body()?.repository}")
                            Log.d("CommitManagerLog", "msg = ${response.body()?.msg}")
                            // =====================================================================

                            // 커밋 내역이 있을 경우 Repository 초기화
                            HomeFragment_TextView_Repository.text = response.body()?.repository.toString()
                            HomeFragment_TextView_Repository.visibility = View.VISIBLE

                            // 커밋 내역이 있고 커밋 메시지도 있을 경우 Msg 초기화
                            if (HomeFragment_TextView_Msg.text != null && HomeFragment_TextView_Msg.text != "") {
                                HomeFragment_TextView_Msg.text = response.body()?.msg.toString()
                                HomeFragment_TextView_Msg.visibility = View.VISIBLE
                            }

                        } else {
                            // 변경 애니메이션 추가할 것
                            // 커밋 내역이 없을 경우 X 이미지로 변경
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_close_black_24dp)

                            // 커밋 내역이 없을 경우 Repository, Msg 숨기기
                            HomeFragment_TextView_Repository.visibility = View.GONE
                            HomeFragment_TextView_Msg       .visibility = View.GONE
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
        val DAY_OF_WEEK_KOR = arrayOf("", "일", "월", "화", "수", "목", "금", "토")

        val cal = Calendar.getInstance()
        var today = cal.get(Calendar.YEAR).toString() + "년 "
        today += (cal.get(Calendar.MONTH) + 1).toString() + "월 "
        today += cal.get(Calendar.DATE).toString() + "일 ("
        today += DAY_OF_WEEK_KOR[cal.get(Calendar.DAY_OF_WEEK)] + ")"

        HomeFragment_TextView_Today.text = today
    }
}