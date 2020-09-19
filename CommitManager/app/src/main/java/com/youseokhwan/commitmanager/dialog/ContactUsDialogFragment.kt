package com.youseokhwan.commitmanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

/**
 * SettingsFragment의 btnContactUs를 클릭하면 나타나는 Dialog
 */
class ContactUsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage("Coming Soon!")
                .setPositiveButton("OK", null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}