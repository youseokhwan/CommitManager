package com.youseokhwan.commitmanager.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.youseokhwan.commitmanager.MainActivity
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_initial.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * 사용자 설정 Fragment
 * @property settingsViewModel
 */
class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var mainActivity = MainActivity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, null)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingsViewModel.text.observe(viewLifecycleOwner, Observer { })

        // '알림 여부' 기존 설정 불러오기
        when (SplashActivity.alarmOption) {
            0 -> view.rbSetNoti01.isChecked = true
            1 -> view.rbSetNoti02.isChecked = true
            2 -> view.rbSetNoti03.isChecked = true
        }

        // '시간' 기존 설정 불러오기
        view.edtSetTime.setText(SplashActivity.alarmTime)

        // '진동' 기존 설정 불러오기
        when (SplashActivity.vibOption) {
            0 -> view.rbSetVib01.isChecked = true
            1 -> view.rbSetVib02.isChecked = true
        }

        // 적용하기 버튼 클릭
        view.btnSave.setOnClickListener {
//            Log.d("CommitManagerLog", "적용하기 버튼 클릭됨")

            // 적용하기 버튼을 누르면
            // 1. 변경된 설정 저장
            mainActivity.updateSettings()

            // 2. AlarmManager를 다시 호출

            // 3. Home Tab으로 Fragment 변경

            // 4. Toast 메시지 "저장되었습니다" 출력
            Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
        }

        // 개발자 소개 버튼 클릭
        view.btnDevList.setOnClickListener {
            Log.d("CommitManagerLog", "개발자 목록 버튼 클릭됨")
        }

        // GitHub 로그아웃 버튼 클릭
        view.btnSetGitHubLogout.setOnClickListener {
            Log.d("CommitManagerLog", "GitHub 로그아웃 버튼 클릭됨")
        }

        return view
    }
}
