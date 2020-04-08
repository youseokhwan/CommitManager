package com.youseokhwan.commitmanager.retrofit

/**
 * Commit DTO
 * @property count 오늘 커밋한 횟수
 * @property lastCommit 최종 커밋 시간
 */
data class Commit (
    val count: Int,
    val lastCommit: String
)