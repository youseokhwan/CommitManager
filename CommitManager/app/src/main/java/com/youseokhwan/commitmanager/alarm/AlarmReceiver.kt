package com.youseokhwan.commitmanager.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.Commit
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var title: String
    private lateinit var text: String

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CommitManagerLog", "AlarmReceiver, onReceive() 호출됨")

        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // title 오늘 날짜로 초기화
        val today = Calendar.getInstance()
        title = (today.get(Calendar.MONTH) + 1).toString() + "월 " + today.get(Calendar.DATE).toString() + "일 커밋"

        // Notification 채널 생성
        createNotificationChannel()

        // 오늘 Commit 여부 확인
        // GET("/commit?id=${id}&token=${token}")
        UserRetrofit.getService().getTodayCommit(id = SplashActivity.id, token = SplashActivity.token)
            .enqueue(object : Callback<Commit> {
                override fun onFailure(call: Call<Commit>?, t: Throwable?) {
                    text = "커밋 내역을 불러오는 중 오류가 발생하였습니다."
                    createNotification(context)
                    throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                }

                override fun onResponse(call: Call<Commit>, response: Response<Commit>) {
                    if (response.isSuccessful) {
                        if (response.body()?.count?:"0" != "0") { // 커밋 내역이 존재할 경우
                            title += " 완료!"
                            text = response.body()?.msg.toString()

                            if (SplashActivity.alarmOption == 2) { // '커밋한 날도 받기'를 선택한 경우
                                createNotification(context)
                            }
                            // =====================================================================
                            // 테스트 - 정상적으로 실행됐는지 확인하기 위한 코드
                            else {
                                Log.d("CommitManagerLog", "정상적으로 호출됐으나 AlarmOption에 의해 알람 생성하지 않음")
                            }
                            // =====================================================================
                        } else { // 커밋 내역이 없을 경우
                            title += " 내역이 없어요"
                            text = "이대로 포기하실 건가요?"
                            createNotification(context)
                        }
                    } else {
                        text = "커밋 내역을 불러오는 중 오류가 발생하였습니다."
                        createNotification(context)

                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
                    }
                }
            })
    }

    /**
     * Notification Channel 생성
     */
    private fun createNotificationChannel() {
        // Oreo 이상
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TodayCommit",
                "Today Commit",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "1일 1커밋 알람"
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Notification 생성
     */
    private fun createNotification(context: Context) {
        // Notification 클릭 시 SplashActivity 실행되도록 설정
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            0
        )

        // Notification 생성
        val builder = NotificationCompat.Builder(context, "TodayCommit")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }
}