package com.youseokhwan.commitmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.youseokhwan.commitmanager.R

/**
 * 월간, 주간 Commit 통계를 표시하는 Fragment
 */
class StatisticsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        return view
    }
}
