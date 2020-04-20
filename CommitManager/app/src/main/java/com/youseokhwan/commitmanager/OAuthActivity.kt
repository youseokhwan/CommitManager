package com.youseokhwan.commitmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_o_auth.*
import org.jetbrains.anko.toast

/**
 * OAuthActivity
 */
class OAuthActivity : AppCompatActivity() {

    companion object {
        var isSuccess: Boolean = false
    }

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
     * isSuccess 값에 따라 로직 진행하고 해당 Activity 종료
     */
    override fun onDestroy() {
        if (isSuccess) {
            toast("OAuth 인증 성공!")
            // 다음 UI Update 진행하는 코드 구현
        } else {
            toast("WebView가 강제 종료됨")
        }

        super.onDestroy()
    }
}
