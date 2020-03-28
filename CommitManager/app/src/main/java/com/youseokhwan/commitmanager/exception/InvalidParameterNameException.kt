package com.youseokhwan.commitmanager.exception

import android.util.Log

/**
 * 유효하지 않은 이름을 인자로 넣은 경우 발생되는 예외
 * @param message 예외 내용
 */
class InvalidParameterNameException(message: String) : Exception(message) {
    init {
        Log.d("CommitManagerLog", message)
    }
}