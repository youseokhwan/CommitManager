package com.youseokhwan.commitmanager.ui.initial

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.youseokhwan.commitmanager.FirstRunActivity
import com.youseokhwan.commitmanager.MainActivity
import com.youseokhwan.commitmanager.R
import kotlinx.android.synthetic.main.fragment_initial.view.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * InitialFragment
 *
 * 최초 실행 시 초기 설정을 진행하는 Fragment
 * 1. GitHub ID를 입력받고 유효성 검사
 * 2. 알림 시간 설정 및 진동 여부 설정
 * @property firstRunActivity
 */
class InitialFragment : Fragment() {

    private var firstRunActivity = FirstRunActivity()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        firstRunActivity = activity as FirstRunActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_initial, null)

        // 시작하기 버튼을 클릭하면 초기 설정을 마침
        view.InitialFragment_Button_Start.setOnClickListener {
            firstRunActivity.finishInitialSettings()
        }

        return view
    }
}
