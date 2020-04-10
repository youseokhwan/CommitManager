package com.youseokhwan.commitmanager.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.youseokhwan.commitmanager.MainActivity
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.Commit
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 오늘 커밋이 있는지 알려주는 이미지와 이번 주 커밋 통계를 표시하는 메인 Fragment
 * @property homeViewModel
 */
class HomeFragment : Fragment() {

    private val mainActivity = MainActivity()
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // 오늘 날짜, 커밋 내역 등 업데이트
        // GET("/commit?id=${id}&token=${token}")
        UserRetrofit.getService().getTodayCommit(id = mainActivity.getUserId(), token = "defaultToken")
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
                            // 변경 애니메이션 추가
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_check_black_24dp)
                        } else {
                            // 변경 애니메이션 추가
                            HomeFragment_ImageView_Daily.setImageResource(R.drawable.ic_close_black_24dp)
                        }
                    } else {
                        toast("오류가 발생했습니다. 다시 시도해주세요.")
                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
                    }
                }
            })

        return root
    }
}
