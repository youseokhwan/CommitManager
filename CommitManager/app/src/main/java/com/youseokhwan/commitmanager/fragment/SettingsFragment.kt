package com.youseokhwan.commitmanager.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import com.youseokhwan.commitmanager.alarm.AlarmOption
import com.youseokhwan.commitmanager.alarm.AlarmReceiver
import com.youseokhwan.commitmanager.alarm.DeviceBootReceiver
import com.youseokhwan.commitmanager.dialog.AlarmTimeDialogFragment
import com.youseokhwan.commitmanager.dialog.ContactUsDialogFragment
import com.youseokhwan.commitmanager.dialog.LogoutDialogFragment
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 사용자 설정 Fragment
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // '알림 여부' 기존 설정 불러오기
        when (SplashActivity.alarmOption) {
            0 -> view.rbSetNoti01.isChecked = true
            1 -> view.rbSetNoti02.isChecked = true
            2 -> view.rbSetNoti03.isChecked = true
        }

        // '시간' 기존 설정 불러오기
        view.edtSetTime.setText(SplashActivity.alarmTime)

        // 알림 시간 팝업 다이얼로그
        view.edtSetTime.setOnClickListener {
            activity?.supportFragmentManager?.let { manager ->
                AlarmTimeDialogFragment(it).show(manager, "settingsAlarm")
            }
        }

        // '진동' 기존 설정 불러오기
        when (SplashActivity.vibOption) {
            0 -> view.rbSetVib01.isChecked = true
            1 -> view.rbSetVib02.isChecked = true
        }

        // 적용하기 버튼 클릭
        view.btnSave.setOnClickListener {
            // 변경된 설정 저장
            val settings = activity?.getSharedPreferences("settings", Context.MODE_PRIVATE) ?: return@setOnClickListener
            with(settings.edit()) {
                // 설정 값을 Companion Object에 저장
                when (view.rgSetNotification.checkedRadioButtonId) {
                    R.id.rbSetNoti01 -> SplashActivity.alarmOption = 0 // 알람 받지 않기
                    R.id.rbSetNoti02 -> SplashActivity.alarmOption = 1 // 커밋 안한 날만 받기
                    R.id.rbSetNoti03 -> SplashActivity.alarmOption = 2 // 커밋한 날도 받기
                }
                SplashActivity.alarmTime = view.edtSetTime.text.toString()
                when (view.rgSetVibrate.checkedRadioButtonId) {
                    R.id.rbSetVib01 -> SplashActivity.vibOption = 0 // 진동
                    R.id.rbSetVib02 -> SplashActivity.vibOption = 1 // 무음
                }

                // 설정 값을 SharedPreferences에 저장
                putInt   ("alarmOption", SplashActivity.alarmOption)
                putString("alarmTime"  , SplashActivity.alarmTime)
                putInt   ("vibOption"  , SplashActivity.vibOption)

                apply()

                Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
            }

            updateAlarmManager()
        }

        // Contact Us 버튼 클릭
        view.btnContactUs.setOnClickListener {
            ContactUsDialogFragment().show(parentFragmentManager, "contactUs")
        }

        // GitHub 로그아웃 버튼 클릭
        view.btnLogout.setOnClickListener {
            LogoutDialogFragment().show(parentFragmentManager, "logout")
        }

        return view
    }

//    /**
//     * showTimePickerDialog()
//     * @param viewId
//     */
//    private fun showTimePickerDialog(viewId: Int) {
//        val cal = Calendar.getInstance()
//        val currentHour = SplashActivity.alarmTime.substring(0..1).toInt()
//        val currentMin = SplashActivity.alarmTime.substring(3..4).toInt()
//
//        when (viewId) {
//            R.id.edtSetTime -> {
//                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
//                    cal.set(Calendar.HOUR_OF_DAY, hour)
//                    cal.set(Calendar.MINUTE     , minute)
//                    edtSetTime
//                        .setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
//                }
//                TimePickerDialog(context, timeSetListener, currentHour, currentMin, true)
//                    .show()
//            }
//            else -> {
//                throw InvalidParameterNameException(
//                    "InvalidParameterNameException: 유효하지 않은 TimePickerDialog 호출입니다."
//                )
//            }
//        }
//    }

    /**
     * 수정된 내용을 AlarmManager에 반영
     */
    private fun updateAlarmManager() {
        Log.d("CommitManagerLog", "updateAlarmManager() 호출됨")

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(activity, 0, it, 0)
        }

        // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
        if (SplashActivity.alarmOption != AlarmOption.NONE.value) {
            val hour = SplashActivity.alarmTime.substring(0..1).toInt()
            val min  = SplashActivity.alarmTime.substring(3..4).toInt()

            // 알림 시간 설정
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE     , min )
                set(Calendar.SECOND     , 0   )
            }

            // 알림 시작
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, // 매일 반복
                alarmIntent
            )

            // 디바이스 재시작 대응
            if (activity != null) {
                val bootReceiver = ComponentName(activity!!, DeviceBootReceiver::class.java)

                activity!!.packageManager.setComponentEnabledSetting(
                    bootReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        } else {
            // AlarmOption이 NONE인 경우 반복 작업 취소
            alarmManager.cancel(alarmIntent)

            // 디바이스 재시작 대응 취소
            if (activity != null) {
                val bootReceiver = ComponentName(activity!!, DeviceBootReceiver::class.java)

                activity!!.packageManager.setComponentEnabledSetting(
                    bootReceiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}
