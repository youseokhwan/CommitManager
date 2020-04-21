package com.youseokhwan.commitmanager

import android.content.Context
import android.webkit.JavascriptInterface

/**
 * AndroidBridge
 */
class AndroidBridge(context: Context) {

    private val oAuthActivity = context as OAuthActivity

    /**
     * WebView에서 OAuth 인증을 마치고 유저의 ID를 Android로 받아오는 메소드
     */
    @JavascriptInterface
    fun getUserId(id: String, token: String) {

        // WebView 정상 종료
        oAuthActivity.transferUserData(id, token)
    }
}