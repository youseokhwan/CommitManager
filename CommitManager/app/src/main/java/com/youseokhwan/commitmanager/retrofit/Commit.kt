package com.youseokhwan.commitmanager.retrofit

/**
 * Commit DTO
 * @property isCommitted 오늘 날짜 커밋 여부
 * @property count 오늘 커밋한 횟수
 * @property lastCommit 최종 커밋 시간
 */
data class Commit (
    val isCommitted: Boolean,
    val count: Int,
    val lastCommit: String
)