package com.youseokhwan.commitmanager.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.youseokhwan.commitmanager.SplashActivity
import java.util.*

/**
 * 재부팅 후에도 푸시 알림이 동작하도록 설정
 */
class DeviceBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CommitManagerLog", "DeviceBootReceiver - onReceive() 호출됨!!")

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(context, 0, it, 0)
            }

            // SharedPreferences에서 데이터 가져오기
            val settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val alarmOption = settings.getInt("alarmOption", AlarmOption.NONE.value)
            val alarmTime = settings.getString("alarmTime", "error")!!

            // AlarmOption 값이 NONE이 아닐 때 AlarmManager 시작
            if (alarmOption != AlarmOption.NONE.value) {
                val hour = alarmTime.substring(0..1).toInt()
                val min  = alarmTime.substring(3..4).toInt()

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

                Toast.makeText(context, "CommitManager 앱을 실행하면 다시 알람이 시작됩니다", Toast.LENGTH_LONG).show()
            } else {
                // AlarmOption이 NONE인 경우 반복 작업 취소
                alarmManager.cancel(alarmIntent)
            }
        } else {
            Log.d("CommitManagerLog", "intent.action == ${intent.action}")
        }
    }
}