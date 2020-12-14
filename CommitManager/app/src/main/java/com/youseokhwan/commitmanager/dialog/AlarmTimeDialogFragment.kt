package com.youseokhwan.commitmanager.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import kotlinx.android.synthetic.main.fragment_initial.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 알람 시간을 설정하는 Dialog
 */
class AlarmTimeDialogFragment(private val v: View) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(
            activity,
//            R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth_Bridge,
            this,
            SplashActivity.alarmTime.substring(0..1).toInt(),
            SplashActivity.alarmTime.substring(3..4).toInt(),
            DateFormat.is24HourFormat(activity)
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)

        // InitialFragment에서 진입
        if (v.id == R.id.edtTime) {
            v.edtTime
                .setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
        }
        // SettingsFragment에서 진입
        else if (v.id == R.id.edtSetTime) {
            v.edtSetTime
                .setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
        }
    }
}