package com.youseokhwan.commitmanager.realm

import io.realm.RealmObject

/**
 * 사이드 기능에 필요한 정보들을 저장하는 모델
 */
open class Commit(
    var thisSun           : Boolean = false,  // 이번 주 일요일 커밋 여부
    var thisMon           : Boolean = false,  // 이번 주 월요일 커밋 여부
    var thisTue           : Boolean = false,  // 이번 주 화요일 커밋 여부
    var thisWed           : Boolean = false,  // 이번 주 수요일 커밋 여부
    var thisThu           : Boolean = false,  // 이번 주 목요일 커밋 여부
    var thisFri           : Boolean = false,  // 이번 주 금요일 커밋 여부
    var thisSat           : Boolean = false,  // 이번 주 토요일 커밋 여부
    var committingStraight: Int     = 0       // 연속 커밋 일수(음수일 경우 연속으로 커밋하지 않은 일수)
) : RealmObject()
