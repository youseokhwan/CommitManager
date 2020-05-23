package com.youseokhwan.commitmanager

/**
 * 알람 설정
 */
enum class AlarmOption(val value: Int) {
    // 알람 받지 않기
    NONE(0),

    // 커밋 안한 날만 받기
    DEFAULT(1),

    // 커밋한 날도 받기
    EVERYDAY(2)
}