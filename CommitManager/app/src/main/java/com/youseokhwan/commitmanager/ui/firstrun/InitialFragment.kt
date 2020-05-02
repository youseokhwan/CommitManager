package com.youseokhwan.commitmanager.ui.firstrun

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.youseokhwan.commitmanager.FirstRunActivity
import com.youseokhwan.commitmanager.OAuthActivity
import com.youseokhwan.commitmanager.R
import com.youseokhwan.commitmanager.SplashActivity
import com.youseokhwan.commitmanager.exception.InvalidParameterNameException
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.retrofit.UserInfo
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import kotlinx.android.synthetic.main.fragment_initial.*
import kotlinx.android.synthetic.main.fragment_initial.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * 최초 실행 시 초기 설정을 진행하는 Fragment
 * 1. GitHub ID를 입력받고 유효성 검사
 * 2. 알림 시간 설정 및 진동 여부 설정
 * @property firstRunActivity
 * @property fadeIn0 FadeIn 애니메이션 0
 * @property fadeIn1 FadeIn 애니메이션 1
 * @property fadeIn2 FadeIn 애니메이션 2
 * @property shake shake 애니메이션
 * @property fadeOut FadeOut 애니메이션
 */
class InitialFragment : Fragment() {

    private var firstRunActivity = FirstRunActivity()
    private lateinit var fadeIn0: Animation
    private lateinit var fadeIn1: Animation
    private lateinit var fadeIn2: Animation
    private lateinit var shake  : Animation
    private lateinit var fadeOut: Animation

    override fun onAttach(context: Context) {
        super.onAttach(context)

        firstRunActivity = activity as FirstRunActivity
        fadeIn0 = AnimationUtils.loadAnimation(context, R.anim.fade_in_0)
        fadeIn1 = AnimationUtils.loadAnimation(context, R.anim.fade_in_1)
        fadeIn2 = AnimationUtils.loadAnimation(context, R.anim.fade_in_2)
        shake   = AnimationUtils.loadAnimation(context, R.anim.shake)
        fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_initial, null)

        // FadeIn 애니메이션
        view.InitialFragment_TextView_GithubIdLabel.startAnimation(fadeIn0)
        view.InitialFragment_Button_GitHubLogin    .startAnimation(fadeIn1)

        // GitHub 로그인 버튼을 클릭하면 WebView 띄우기
        view.InitialFragment_Button_GitHubLogin.setOnClickListener {
            // OAuthActivity 호출
            startActivity<OAuthActivity>()
        }

        // 알림 시간 팝업 다이얼로그(First)
        view.InitialFragment_EditText_Time.setOnClickListener {
            showTimePickerDialog(it.id)
        }

        // 시작하기 버튼을 클릭하면 Username을 받아서 저장하고 초기 설정을 마침
        view.InitialFragment_Button_Start.setOnClickListener {
            // GET("/userinfo?id=${id}")
            UserRetrofit.getService().getUserInfo(id = InitialFragment_EditText_GithubId.text.toString())
                .enqueue(object : Callback<UserInfo> {
                    override fun onFailure(call: Call<UserInfo>?, t: Throwable?) {
                        toast("오류가 발생했습니다. 다시 시도해주세요.")
                        throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                    }

                    override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                        if (response.isSuccessful) {
                            Log.d("CommitManagerLog", response.body().toString())

                            firstRunActivity.finishInitialSettings(response.body())
                        } else {
                            toast("오류가 발생했습니다. 다시 시도해주세요.")
                            throw RetrofitException("RetrofitException: response.isSuccessful is false")
                        }
                    }
                })
        }

        return view
    }

    /**
     * GitHub ID의 유효성 검사 후 UI 변경
     */
//    private fun githubIdCheck() {
//        // EditText의 내용이 없을 경우
//        if (InitialFragment_EditText_GithubId.text.isEmpty()) {
//            changeVerifyUi("null")
//            return
//        }
//
//        // GET("/user?id=${id}")
//        UserRetrofit.getService().idCheck(id = InitialFragment_EditText_GithubId.text.toString())
//            .enqueue(object : Callback<User> {
//                override fun onFailure(call: Call<User>?, t: Throwable?) {
//                    changeVerifyUi("error")
//                    throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
//                }
//
//                override fun onResponse(call: Call<User>, response: Response<User>) {
//                    if (response.isSuccessful) {
//                        Log.d("CommitManagerLog", response.body().toString())
//
//                        val user: User? = response.body()
//
//                        if (user?.isExist == true) {
//                            changeVerifyUi("yes")
//                        } else {
//                            changeVerifyUi("no")
//                        }
//                    } else {
//                        changeVerifyUi("error")
//                        throw RetrofitException("RetrofitException: response.isSuccessful is false")
//                    }
//                }
//            })
//    }

    /**
     * InitialFragment_TextView_Verify의 UI를 변경
     * @param status 상태 값
     */
    // =============================================================================================
    // 수정 필요
    // =============================================================================================
//    private fun changeVerifyUi(status: String) {
//        when (status) {
//            "yes" -> {
//                changeBottomUiState("enabled")
//            }
//            "no" -> {
////                InitialFragment_TextView_Verify.startAnimation(shake)
////                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_no)
////                InitialFragment_TextView_Verify.textColor = Color.RED
//            }
//            "null" -> {
////                InitialFragment_TextView_Verify.startAnimation(shake)
////                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_null)
////                InitialFragment_TextView_Verify.textColor = Color.RED
//            }
//            "error" -> {
////                InitialFragment_TextView_Verify.startAnimation(shake)
////                InitialFragment_TextView_Verify.text      = getString(R.string.label_verify_error)
////                InitialFragment_TextView_Verify.textColor = Color.RED
//            }
//            else -> {
//                throw InvalidParameterNameException(
//                    "InvalidParameterNameException: ${status}은 유효하지 않은 VerifyStatus 입니다."
//                )
//            }
//        }
//
////        InitialFragment_TextView_Verify.visibility = View.VISIBLE
//    }

    /**
     * GitHub ID Check 시 Start 버튼 및 하단 UI의 상태 값 변경
     * @param status 상태 값
     */
//    private fun changeBottomUiState(status: String) {
//        when (status) {
//            "enabled" -> {
//                // 알림 여부
//                InitialFragment_TextView_NotificationLabel.visibility = View.VISIBLE
//                InitialFragment_RadioGroup_Notification   .visibility = View.VISIBLE
//
//                // 시간
//                InitialFragment_TextView_Time             .visibility = View.VISIBLE
//                InitialFragment_EditText_Time             .visibility = View.VISIBLE
//
//                // 진동
//                InitialFragment_TextView_Vibrate          .visibility = View.VISIBLE
//                InitialFragment_RadioGroup_Vibrate        .visibility = View.VISIBLE
//
//                // 시작하기 버튼
//                InitialFragment_Button_Start              .textColor  = ContextCompat.getColor(context!!, R.color.limegreen)
//                InitialFragment_Button_Start              .isEnabled  = true
//
////                InitialFragment_TextView_NotificationLabel.startAnimation(fadeIn0)
////                InitialFragment_TextView_Time             .startAnimation(fadeIn1)
////                InitialFragment_EditText_Time             .startAnimation(fadeIn1)
//
//                checkStartButtonState()
//            }
//            "disabled" -> {
//                if (InitialFragment_TextView_NotificationLabel.visibility == View.VISIBLE) {
//                    // 알림 여부
//                    InitialFragment_TextView_NotificationLabel.visibility = View.INVISIBLE
//                    InitialFragment_RadioGroup_Notification   .visibility = View.INVISIBLE
//
//                    // 시간
//                    InitialFragment_TextView_Time             .visibility = View.INVISIBLE
//                    InitialFragment_EditText_Time             .visibility = View.INVISIBLE
//
//                    // 진동
//                    InitialFragment_TextView_Vibrate          .visibility = View.INVISIBLE
//                    InitialFragment_RadioGroup_Vibrate        .visibility = View.INVISIBLE
//
//                    // 시작하기 버튼
//                    InitialFragment_Button_Start              .textColor  = ContextCompat.getColor(context!!, R.color.gray)
//                    InitialFragment_Button_Start              .isEnabled  = false
//
////                    InitialFragment_TextView_NotificationLabel.startAnimation(fadeOut)
////                    InitialFragment_TextView_Time             .startAnimation(fadeOut)
////                    InitialFragment_EditText_Time             .startAnimation(fadeOut)
//                }
//            }
//            else -> {
//                throw InvalidParameterNameException(
//                    "InvalidParameterNameException: ${status}은 유효하지 않은 StartButtonState 입니다."
//                )
//            }
//        }
//    }

    /**
     * showTimePickerDialog()
     * @param viewId
     */
    private fun showTimePickerDialog(viewId: Int) {
        val cal = Calendar.getInstance()

        when (viewId) {
            R.id.InitialFragment_EditText_Time -> {
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    InitialFragment_EditText_Time
                        .setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
                    checkStartButtonState()
                }
                TimePickerDialog(context, timeSetListener, 18,0, true)
                    .show()
            }
            else -> {
                throw InvalidParameterNameException(
                    "InvalidParameterNameException: 유효하지 않은 TimePickerDialog 호출입니다."
                )
            }
        }
    }

    /**
     * 입력 값 변경 시 Start 버튼의 유효성을 검사하고 상태 값 변경
     */
    // =============================================================================================
    // 수정 필요
    // =============================================================================================
    private fun checkStartButtonState() {
        // 문제되는 사항이 없으면 enable
        InitialFragment_Button_Start.textColor = ContextCompat.getColor(context!!, R.color.limegreen)
        InitialFragment_Button_Start.isEnabled = true
    }

    /**
     * OAuth 인증에 성공하면 UI 변경
     */
    fun showBottomUi() {
        // GitHub 로그인 버튼, ID EditText
        InitialFragment_Button_GitHubLogin.visibility = View.INVISIBLE
        InitialFragment_EditText_GithubId .visibility = View.VISIBLE
        InitialFragment_EditText_GithubId .setText(SplashActivity.id)

        // 알림 여부
        InitialFragment_TextView_NotificationLabel.visibility = View.VISIBLE
        InitialFragment_RadioGroup_Notification   .visibility = View.VISIBLE

        // 시간
        InitialFragment_TextView_Time.visibility = View.VISIBLE
        InitialFragment_EditText_Time.visibility = View.VISIBLE

        // 진동
        InitialFragment_TextView_Vibrate.visibility = View.VISIBLE
        InitialFragment_RadioGroup_Vibrate.visibility = View.VISIBLE

        // 시작하기 버튼
        InitialFragment_Button_Start.textColor  = ContextCompat.getColor(context!!, R.color.limegreen)
        InitialFragment_Button_Start.isEnabled  = true
    }

    override fun onStart() {
        // OAuth 인증에 성공하여 저장된 id가 존재하는 경우
        if (SplashActivity.id != "") {
            showBottomUi()
        }

        super.onStart()
    }
}
