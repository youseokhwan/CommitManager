package com.youseokhwan.commitmanager.ui.githubid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.youseokhwan.commitmanager.R

/**
 * GitHubIdFragment
 *
 * GitHub ID를 입력받고 유효성을 검사하는 Fragment
 */
class GitHubIdFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_githubid, null)
    }
}
