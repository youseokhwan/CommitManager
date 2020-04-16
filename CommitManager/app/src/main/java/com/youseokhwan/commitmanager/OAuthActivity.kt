package com.youseokhwan.commitmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }

    /**
     * WebView가 종료될 때 정상 종료 여부 판단하여 유저 정보 저장
     */
    override fun onDestroy() {
        // 정상 종료 여부 판단
        // 유저 정보 저장

        return super.onDestroy()
    }
}
