package com.youseokhwan.commitmanager.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.youseokhwan.commitmanager.*
import com.youseokhwan.commitmanager.dialog.AlarmTimeDialogFragment
import com.youseokhwan.commitmanager.exception.RetrofitException
import com.youseokhwan.commitmanager.realm.User
import com.youseokhwan.commitmanager.retrofit.UserInfo
import com.youseokhwan.commitmanager.retrofit.UserRetrofit
import com.youseokhwan.commitmanager.webview.OAuthActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_initial.*
import kotlinx.android.synthetic.main.fragment_initial.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private lateinit var realm: Realm

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_initial, container, false)

        // FadeIn 애니메이션
        view.txtGithubIdLabel.startAnimation(fadeIn0)
        view.btnGitHubLogin  .startAnimation(fadeIn1)

        // GitHub 로그인 버튼을 클릭하면 WebView 띄우기
        view.btnGitHubLogin.setOnClickListener {
            // OAuthActivity 호출
            startActivityForResult(Intent(activity, OAuthActivity::class.java), 0)

            // GitHub 로그인 버튼 숨기고 Loading 띄우기
        }

        // 알림 여부 RadioGroup
        view.rgNotification.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                // 알림 받지 않기 선택하면 UI 숨기기
                R.id.rbNoti01 -> {
                    txtTime   .visibility = View.INVISIBLE
                    edtTime   .visibility = View.INVISIBLE
                    txtVibrate.visibility = View.INVISIBLE
                    rgVibrate .visibility = View.INVISIBLE

                    txtTime   .startAnimation(fadeOut)
                    edtTime   .startAnimation(fadeOut)
                    txtVibrate.startAnimation(fadeOut)
                    rgVibrate .startAnimation(fadeOut)
                }
                // 그 외는 UI 표시
                else -> {
                    if (txtTime.visibility == View.INVISIBLE) {
                        txtTime   .visibility = View.VISIBLE
                        edtTime   .visibility = View.VISIBLE
                        txtVibrate.visibility = View.VISIBLE
                        rgVibrate .visibility = View.VISIBLE

                        txtTime   .startAnimation(fadeIn0)
                        edtTime   .startAnimation(fadeIn0)
                        txtVibrate.startAnimation(fadeIn1)
                        rgVibrate .startAnimation(fadeIn1)
                    }
                }
            }
        }

        // 알림 시간 팝업 다이얼로그
        view.edtTime.setOnClickListener {
            activity?.supportFragmentManager?.let { manager ->
                AlarmTimeDialogFragment(it).show(manager, "initialAlarm")
            }
        }

        // 시작하기 버튼을 클릭하면 Username을 받아서 저장하고 초기 설정을 마침
        view.btnStart.setOnClickListener {
            // GET("/userinfo?id=${id}")
            UserRetrofit.getService().getUserInfo(id = edtGithubId.text.toString())
                .enqueue(object : Callback<UserInfo> {
                    override fun onFailure(call: Call<UserInfo>?, t: Throwable?) {
                        Toast.makeText(context, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        throw RetrofitException("RetrofitException: onFailure()\n${t.toString()}")
                    }

                    override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                        if (response.isSuccessful) {
                            Log.d("CommitManagerLog", response.body().toString())

                            // 데이터 저장 후 MainActivity로 전환
                            firstRunActivity.finishInitialSettings(response.body())
                        } else {
                            Toast.makeText(context, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            throw RetrofitException("RetrofitException: response.isSuccessful is false")
                        }
                    }
                })
        }

        return view
    }

//    /**
//     * showTimePickerDialog()
//     * @param viewId
//     */
//    private fun showTimePickerDialog(viewId: Int) {
//        val cal = Calendar.getInstance()
//
//        when (viewId) {
//            R.id.edtTime -> {
//                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
//                    cal.set(Calendar.HOUR_OF_DAY, hour)
//                    cal.set(Calendar.MINUTE     , minute)
//                    edtTime
//                        .setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
//                }
//                TimePickerDialog(context, timeSetListener, 18,0, true)
//                    .show()
//            }
//            else -> {
//                throw InvalidParameterNameException(
//                    "InvalidParameterNameException: 유효하지 않은 TimePickerDialog 호출입니다."
//                )
//            }
//        }
//    }

    /**
     * OAuth 인증에 성공하면 UI 변경
     */
    private fun showBottomUI() {
        val userItem = realm.where<User>().findFirst()

        // GitHub 로그인 버튼, ID EditText
        btnGitHubLogin.visibility = View.INVISIBLE
        edtGithubId   .visibility = View.VISIBLE
        edtGithubId   .setText(userItem?.id ?: "error")

        // 알림 여부
        txtNotificationLabel.visibility = View.VISIBLE
        rgNotification      .visibility = View.VISIBLE

        // 시간
        txtTime.visibility = View.VISIBLE
        edtTime.visibility = View.VISIBLE

        // 진동
        txtVibrate.visibility = View.VISIBLE
        rgVibrate .visibility = View.VISIBLE

        // 시작하기 버튼
        btnStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.limegreen))
        btnStart.isEnabled  = true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**
     * OAuthActivity가 종료될 때 id, token을 받아서 User 데이터 생성
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("CommitManagerLog",
                "RESULT_OK: ${data?.getStringExtra("id")}, ${data?.getStringExtra("token")}")

            // User 데이터 생성
            realm.executeTransactionAsync({
                val userItem = it.createObject<User>()
                userItem.id        = data?.getStringExtra("id")    ?: "error"
                userItem.token     = data?.getStringExtra("token") ?: "error"
                userItem.name      = ""
                userItem.imgSrc    = ""
                userItem.follower  = 0
                userItem.following = 0
            }, {
                showBottomUI() // 설정 UI 보이기
            }, {
                Log.d("CommitManagerLog", "executeTransactionAsync: onError()")
                Log.d("CommitManagerLog", "오류 메시지: ${it.message}")

                // 로그인에 실패했으므로 Loading을 지우고 다시 GitHub 로그인 버튼을 띄워야 함
            })
        } else {
            Log.d("CommitManagerLog", "onActivityResult else문 진입")

            // 로그인에 실패했으므로 Loading을 지우고 다시 GitHub 로그인 버튼을 띄워야 함
        }
    }
}
