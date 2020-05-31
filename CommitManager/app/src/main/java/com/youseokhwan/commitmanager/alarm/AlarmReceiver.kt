package com.youseokhwan.commitmanager.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.youseokhwan.commitmanager.MainActivity
import com.youseokhwan.commitmanager.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        // Oreo 이후 버전 기준으로 작성함
        val builder = NotificationCompat.Builder(context, "default")
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)

        val channelName = "Daily Commit"
//        val description = "1일 1커밋 알람"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel("dailyCommit", channelName, importance)
//        channel.description(description)

        notificationManager.createNotificationChannel(channel)

        builder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setTicker("{Time to watch some cool stuff!}")  // 무슨 뜻?
            .setContentTitle("Title")
            .setContentText("SubTitle")
            .setContentInfo("INFO")
            .setContentIntent(pendingIntent)

        notificationManager.notify(1001, builder.build())
    }
}