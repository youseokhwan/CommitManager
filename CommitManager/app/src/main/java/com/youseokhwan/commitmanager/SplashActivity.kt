package com.youseokhwan.commitmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.youseokhwan.commitmanager.realm.User
import io.realm.Realm
import io.realm.kotlin.where

/**
 * SplashActivity
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var realm: Realm  // Realm 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Realm 초기화
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        // Record 검색해서 결과없으면 isFirstRun이 True임
        val isFirstRun = realm.where<User>().findAll().count() == 0
        Log.d("RealmLog", "Realm FirstRun: $isFirstRun")

        if (isFirstRun) {
            // FirstRunActivity로 이동
            startActivity(Intent(this, FirstRunActivity::class.java))
            finish()
        } else {
            // MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()  // Realm 메모리 해제
    }
}
