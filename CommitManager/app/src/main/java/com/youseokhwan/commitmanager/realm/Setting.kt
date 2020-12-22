package com.youseokhwan.commitmanager.realm

import io.realm.RealmObject

/**
 * 설정 정보를 저장하는 모델
 */
open class Setting(
    var isFirstRun : Boolean = true,     // 첫 번째 실행 여부
    var alarmOption: Int     = 0,        // 알람 옵션
    var alarmTime  : String  = "",       // 알람 시간
    var vibOption  : Int     = 0         // 진동 옵션
) : RealmObject()