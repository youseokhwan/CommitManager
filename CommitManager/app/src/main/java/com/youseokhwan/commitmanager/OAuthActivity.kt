package com.youseokhwan.commitmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.youseokhwan.commitmanager.ui.firstrun.InitialFragment
import kotlinx.android.synthetic.main.activity_o_auth.*
import kotlinx.android.synthetic.main.fragment_initial.*
import org.jetbrains.anko.toast

/**
 * OAuthActivity
 * @property isSuccess 로그인 성공 여부 판단
 */
class OAuthActivity : AppCompatActivity() {

    var isSuccess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_auth)

        // WebView 설정
        OAuthActivity_WebView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
        OAuthActivity_WebView.loadUrl("http://ec2-18-223-112-230.us-east-2.compute.amazonaws.com:3001/login")

        // Bridge 설정
        val bridge: AndroidBridge = AndroidBridge(this)
        OAuthActivity_WebView.addJavascriptInterface(bridge, "Android")
    }

    /**
     * 유저 데이터를 Web에서 Android로 전송
     */
    fun transferUserData(id: String, token: String) {
        Log.d("CommitManagerLog", "Login ID: $id")
        Log.d("CommitManagerLog", "Token: $token")

        // 데이터를 SplashActivity의 Companion Object 변수에 저장
        SplashActivity.id = id
        SplashActivity.token = token

        isSuccess = true
        finish()
    }

    override fun finish() {
        if (isSuccess) {
            toast("OAuth 인증 성공!")
        } else {
            toast("WebView가 강제 종료됨")
        }

        super.finish()
    }
}
