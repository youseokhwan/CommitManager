package com.youseokhwan.commitmanager.dialog

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.youseokhwan.commitmanager.SplashActivity
import com.youseokhwan.commitmanager.alarm.AlarmOption
import com.youseokhwan.commitmanager.alarm.AlarmReceiver
import com.youseokhwan.commitmanager.alarm.DeviceBootReceiver
import com.youseokhwan.commitmanager.alarm.VibOption
import java.lang.IllegalStateException

/**
 * SettingsFragment의 btnLogout을 클릭하면 나타나는 Dialog
 */
class LogoutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage("로그아웃 후 앱이 재시작됩니다.")
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        // AlarmManager 반복 작업 취소
                        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                            PendingIntent.getBroadcast(context, 0, intent, 0)
                        }
                        alarmManager.cancel(alarmIntent)

                        // 디바이스 재시작 대응 취소
                        if (context != null) {
                            val bootReceiver = ComponentName(context!!, DeviceBootReceiver::class.java)

                            context!!.packageManager.setComponentEnabledSetting(
                                bootReceiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP
                            )
                        }

                        // 데이터 초기화
                        val settings = activity?.getSharedPreferences("settings", Context.MODE_PRIVATE) ?: return@OnClickListener
                        with(settings.edit()) {
                            // Companion Object 값 초기화
                            SplashActivity.isFirstRun  = true                   // 최초 실행 여부
                            SplashActivity.id          = ""                     // User ID
                            SplashActivity.token       = ""                     // OAuth Token
                            SplashActivity.alarmOption = AlarmOption.NONE.value // 알람 옵션
                            SplashActivity.alarmTime   = "22:00"                // 알람 시간
                            SplashActivity.vibOption   = VibOption.VIB.value    // 진동 옵션
                            SplashActivity.name        = ""                     // Username
                            SplashActivity.imgSrc      = ""                     // Avartar 이미지 경로
                            SplashActivity.follower    = 0                      // Follower 수
                            SplashActivity.following   = 0                      // Following 수

                            // SharedPreferences 초기화
                            clear().apply()
                        }

                        // 앱 재시작
                        val intent = Intent(context, SplashActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        activity?.finishAffinity()
                    })
                .setNegativeButton("Cancel", null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}