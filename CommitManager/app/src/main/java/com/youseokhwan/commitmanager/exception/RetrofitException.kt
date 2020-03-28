package com.youseokhwan.commitmanager.exception

import android.util.Log

/**
 * Retrofit 구문 실행 중 발생되는 오류에 대한 명시적인 예외
 * @param message 예외 내용
 */
class RetrofitException(message: String) : Exception(message) {
    init {
        Log.d("CommitManagerLog", message)
    }
}