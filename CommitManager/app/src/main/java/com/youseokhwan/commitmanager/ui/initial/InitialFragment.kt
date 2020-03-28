package com.youseokhwan.commitmanager.ui.initial

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.youseokhwan.commitmanager.FirstRunActivity
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.User
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import kotlinx.android.synthetic.main.fragment_initial.*
import kotlinx.android.synthetic.main.fragment_initial.view.*
import kotlinx.android.synthetic.main.fragment_initial.view.InitialFragment_EditText_GithubId
import org.jetbrains.anko.textColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
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

        // EditText의 내용이 변경되면 하단 UI Invisible, Disabled 처리
        view.InitialFragment_EditText_GithubId.doOnTextChanged { _, _, _, _ ->
            InitialFragment_TextView_Verify.text = ""
            changeStartButtonState("disabled")
        }

        // EditText IME_ACTION_DONE 이벤트 발생 시 GitHubIdCheck 클릭 이벤트 발생
        view.InitialFragment_EditText_GithubId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("CommitManagerLog", "DONE !!")
                view.InitialFragment_Button_GitHubIdCheck.performClick()
                true
            } else {
                false
            }
        }

        // 확인 버튼을 클릭하면 GitHub ID의 유효성을 검사
        view.InitialFragment_Button_GitHubIdCheck.setOnClickListener {
            Log.d("CommitManagerLog",
                "입력된 GitHub ID: ${InitialFragment_EditText_GithubId.text}")

            InitialFragment_TextView_Verify.text = ""
            hideKeyboard()
            githubIdCheck()
        }

        // 시작하기 버튼을 클릭하면 초기 설정을 마침
        view.InitialFragment_Button_Start.setOnClickListener {
            firstRunActivity.finishInitialSettings()
        }

        return view
    }

    /**
     * GitHub ID의 유효성 검사 후 UI 변경
     */
    private fun githubIdCheck() {
        // EditText의 내용이 없을 경우
        if (InitialFragment_EditText_GithubId.text.isEmpty()) {
            changeVerifyUi("null")
            return
        }

        // GET("/user?id=${id}")
        UserRetrofit.getService().idCheck(id = InitialFragment_EditText_GithubId.text.toString())
            .enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>?, t: Throwable?) {
                    changeVerifyUi("error")
                    throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Log.d("CommitManagerLog", response.body().toString())

                        val user: User? = response.body()

                        if (user?.isExist == true) {
                            changeVerifyUi("yes")
                        } else {
                            changeVerifyUi("no")
                        }
                    } else {
                        changeVerifyUi("error")
                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
                    }
                }
            })
    }

    /**
     * InitialFragment_TextView_Verify의 UI를 변경
     * @param status 상태 값
     */
    private fun changeVerifyUi(status: String) {
        when (status) {
            "yes" -> {
                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_yes)
                InitialFragment_TextView_Verify.textColor = ContextCompat.getColor(context!!, R.color.limegreen)
                changeStartButtonState("enabled")
            }
            "no" -> {
                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_no)
                InitialFragment_TextView_Verify.textColor = Color.RED
            }
            "null" -> {
                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_null)
                InitialFragment_TextView_Verify.textColor = Color.RED
            }
            "error" -> {
                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_error)
                InitialFragment_TextView_Verify.textColor = Color.RED
            }
            else -> {
                throw InvalidParameterNameException(
                    "InvalidParameterNameException: ${status}은 유효하지 않은 VerifyStatus 입니다."
                )
            }
        }

        InitialFragment_TextView_Verify.visibility = View.VISIBLE
    }

    /**
     * Start 버튼 및 하단 UI의 Enable, Visible 상태 값 변경
     * @param status 상태 값
     */
    private fun changeStartButtonState(status: String) {
        when (status) {
            "enabled" -> {
                InitialFragment_TextView_Time.visibility   = View.VISIBLE
                InitialFragment_TextView_First.visibility  = View.VISIBLE
                InitialFragment_TextView_Second.visibility = View.VISIBLE
                InitialFragment_EditText_First.visibility  = View.VISIBLE
                InitialFragment_EditText_Second.visibility = View.VISIBLE
                InitialFragment_CheckBox_Second.visibility = View.VISIBLE
                InitialFragment_Button_Start.textColor     = ContextCompat.getColor(context!!, R.color.limegreen)
                InitialFragment_Button_Start.isEnabled     = true
            }
            "disabled" -> {
                InitialFragment_TextView_Time.visibility   = View.INVISIBLE
                InitialFragment_TextView_First.visibility  = View.INVISIBLE
                InitialFragment_TextView_Second.visibility = View.INVISIBLE
                InitialFragment_EditText_First.visibility  = View.INVISIBLE
                InitialFragment_EditText_Second.visibility = View.INVISIBLE
                InitialFragment_CheckBox_Second.visibility = View.INVISIBLE
                InitialFragment_Button_Start.textColor     = ContextCompat.getColor(context!!, R.color.gray)
                InitialFragment_Button_Start.isEnabled     = false
            }
            else -> {
                throw InvalidParameterNameException(
                    "InvalidParameterNameException: ${status}은 유효하지 않은 StartButtonState 입니다."
                )
            }
        }
    }

    /**
     * 키보드 숨기기
     */
    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
