package com.youseokhwan.commitmanager.retrofit

/**
 * Commit DTO
 * @property count 오늘 커밋한 횟수
 * @property lastCommit 최종 커밋 시간
 * @property repository 최종 커밋 레포지토리
 * @property msg 최종 커밋 메시지
 */
data class Commit (
    val count: Int,
    val lastCommit: String,
    val repository: String,
    val msg: String
)