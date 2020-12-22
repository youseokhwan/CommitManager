package com.youseokhwan.commitmanager.webview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import com.youseokhwan.commitmanager.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_o_auth.*

/**
 * OAuthActivity
 * @property isSuccess 로그인 성공 여부 판단
 */
class OAuthActivity : AppCompatActivity() {

    private var isSuccess = false
    private lateinit var realm: Realm
    private var id = "default"
    private var token = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_auth)

        // Realm 인스턴스 초기화
        realm = Realm.getDefaultInstance()

        // WebView 설정
        wv.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
        wv.loadUrl("http://ec2-18-223-112-230.us-east-2.compute.amazonaws.com:3001/login")

        // Bridge 설정
        val bridge = AndroidBridge(this)
        wv.addJavascriptInterface(bridge, "Android")
    }

    /**
     * 유저 데이터를 WebView에서 Android로 전송
     */
    fun transferUserData(id: String, token: String) {
        Log.d("CommitManagerLog", "Login ID: $id")
        Log.d("CommitManagerLog", "Token: $token")

        this.id = id
        this.token = token
        this.isSuccess = true

        Log.d("CommitManagerLog", "설정 후: ${this.id}, ${this.token}")

//        // 데이터를 SplashActivity의 Companion Object 변수에 저장
//        SplashActivity.id = id
//        SplashActivity.token = token

        finish()
    }

    /**
     * Webview에서 받아온 유저 정보를 통해 User 레코드 생성
     */
//    private fun createUserItem() {
//        realm.executeTransactionAsync({ bgRealm ->
//            val userItem = bgRealm.createObject<User>()
//            userItem.id        = this.id
//            userItem.token     = this.token
//            userItem.name      = ""
//            userItem.imgSrc    = ""
//            userItem.follower  = 0
//            userItem.following = 0
//        }, {
//            Log.d("CommitManagerLog", "executeTransactionAsync -> onSuccess() 호출")
//        }, {
//            Log.d("CommitManagerLog", "executeTransactionAsync -> onError() 호출")
//            Log.d("CommitManagerLog", "오류 메시지: ${it.message}")
//        })
//    }

    override fun finish() {
        val intent = Intent()

        if (isSuccess) {
            Toast.makeText(applicationContext, "OAuth 인증 성공!", Toast.LENGTH_SHORT).show()

            intent.putExtra("id", this.id)
            intent.putExtra("token", this.token)

            setResult(RESULT_OK, intent)
        } else {
            Toast.makeText(applicationContext, "WebView가 강제 종료됨", Toast.LENGTH_SHORT).show()

            intent.putExtra("id", this.id)
            intent.putExtra("token", this.token)

            setResult(RESULT_CANCELED, intent)
        }

        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
//        createUserItem()  // User 데이터 삽입(최초 생성)
        realm.close()
    }
}
