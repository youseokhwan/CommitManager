package com.youseokhwan.commitmanager.ui.welcome

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.youseokhwan.commitmanager.FirstRunActivity
import com.youseokhwan.commitmanager.R
import kotlinx.android.synthetic.main.fragment_welcome.view.*
import org.jetbrains.anko.support.v4.toast

/**
 * WelcomeFragment
 *
 * 환영 문구를 출력하는 Fragment
 * @property firstRunActivity
 */
class WelcomeFragment : Fragment() {

    private var firstRunActivity = FirstRunActivity()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        firstRunActivity = activity as FirstRunActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, null)

        // 다음 버튼을 클릭하면 GithubIdFragment로 전환
        view.btn_welcome.setOnClickListener {
            firstRunActivity.onFragmentChange("githubid")
        }

        return view
    }
}
