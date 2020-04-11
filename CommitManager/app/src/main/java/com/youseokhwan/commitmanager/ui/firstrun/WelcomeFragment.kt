package com.youseokhwan.commitmanager.ui.firstrun

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.youseokhwan.commitmanager.FirstRunActivity
import com.youseokhwan.commitmanager.R
import kotlinx.android.synthetic.main.fragment_welcome.view.*

/**
 * 환영 문구를 출력하는 Fragment
 * @property firstRunActivity
 * @property fadeIn0 FadeIn 애니메이션 1
 * @property fadeIn1 FadeIn 애니메이션 2
 */
class WelcomeFragment : Fragment() {

    private var firstRunActivity = FirstRunActivity()
    private lateinit var fadeIn0: Animation
    private lateinit var fadeIn1: Animation

    override fun onAttach(context: Context) {
        super.onAttach(context)

        firstRunActivity = activity as FirstRunActivity
        fadeIn0 = AnimationUtils.loadAnimation(context, R.anim.fade_in_0)
        fadeIn1 = AnimationUtils.loadAnimation(context, R.anim.fade_in_1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, null)

        // FadeIn 애니메이션
        view.WelcomeFragment_TextView_Title   .startAnimation(fadeIn0)
        view.WelcomeFragment_TextView_Contents.startAnimation(fadeIn1)
        view.WelcomeFragment_Button_Next      .startAnimation(fadeIn1)

        // 다음 버튼을 클릭하면 InitialFragment로 전환
        view.WelcomeFragment_Button_Next.setOnClickListener {
            firstRunActivity.onFragmentChange("initial")
        }

        return view
    }
}
