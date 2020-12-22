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
import com.youseokhwan.commitmanager.alarm.AlarmReceiver
import com.youseokhwan.commitmanager.alarm.DeviceBootReceiver
import com.youseokhwan.commitmanager.realm.Commit
import com.youseokhwan.commitmanager.realm.Setting
import com.youseokhwan.commitmanager.realm.User
import io.realm.Realm
import io.realm.kotlin.where
import java.lang.IllegalStateException

/**
 * SettingsFragment의 btnLogout을 클릭하면 나타나는 Dialog
 */
class LogoutDialogFragment : DialogFragment() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

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
                            val bootReceiver = ComponentName(requireContext(), DeviceBootReceiver::class.java)

                            requireContext().packageManager.setComponentEnabledSetting(
                                bootReceiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP
                            )
                        }

                        // 데이터 초기화
                        realm.beginTransaction()

                        val userItem = realm.where<User>().findAll()
                        userItem.deleteAllFromRealm()

                        val settingItem = realm.where<Setting>().findAll()
                        settingItem.deleteAllFromRealm()

                        val commitItem = realm.where<Commit>().findAll()
                        commitItem.deleteAllFromRealm()

                        // =========================================================================
                        // 추후 Achievement 모델 초기화 구현
                        // =========================================================================

                        realm.commitTransaction()

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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}