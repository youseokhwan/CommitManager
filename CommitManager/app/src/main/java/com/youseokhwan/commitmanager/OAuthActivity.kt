package com.youseokhwan.commitmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_o_auth.*

/**
 * OAuthActivity
 */
class OAuthActivity : AppCompatActivity() {

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
        val bridge: AndroidBridge = AndroidBridge()
        OAuthActivity_WebView.addJavascriptInterface(bridge, "Android")
    }

    /**
     * 로그인 성공
     */
    fun loginSuccess() {
        Log.d("CommitManagerLog", "로그인 성공")
        finish()
    }

    /**
     * 로그인 실패
     */
    fun loginFailure() {
        Log.d("CommitManagerLog", "로그인 실패")
        finish()
    }
}
