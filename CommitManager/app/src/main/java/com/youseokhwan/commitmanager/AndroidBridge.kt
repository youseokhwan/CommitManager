package com.youseokhwan.commitmanager

import android.util.Log
import android.webkit.JavascriptInterface

/**
 * AndroidBridge
 */
class AndroidBridge {

    /**
     * WebView에서 OAuth 인증을 마치고 유저의 ID를 Android로 받아오는 메소드
     */
    @JavascriptInterface
    fun getUserId(id: String, token: String) {
        Log.d("CommitManagerLog", "Login ID: $id")
        Log.d("CommitManagerLog", "Token: $token")
    }
}