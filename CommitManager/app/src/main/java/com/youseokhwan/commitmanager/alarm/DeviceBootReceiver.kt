package com.youseokhwan.commitmanager.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.youseokhwan.commitmanager.SplashActivity
import java.util.*

/**
 * 재부팅 후에도 푸시 알림이 동작하도록 설정
 */
class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Objects.equals(intent!!.action, "android.intent.action.BOOT_COMPLETED")) {
            val pendingIntent = PendingIntent.getBroadcast(context, 0,
                Intent(context, AlarmReceiver::class.java), 0)
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // String 타입인 SplashActivity.alarmTime 값을 Calendar 타입으로 변환
            val time = Calendar.getInstance()
            time.set(Calendar.HOUR  , SplashActivity.alarmTime.substring(0..1).toInt())
            time.set(Calendar.MINUTE, SplashActivity.alarmTime.substring(3..4).toInt())

            // 현재 시간
            val now = Calendar.getInstance()

            // 설정한 시간이 현재 시간을 지났으면 DAY + 1
            if (time.after(now)) {
                time.add(Calendar.DATE, 1)
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }
}