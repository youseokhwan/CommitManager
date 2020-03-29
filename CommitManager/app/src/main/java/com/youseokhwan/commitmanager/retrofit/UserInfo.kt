package com.youseokhwan.commitmanager.retrofit

/**
 * UserInfo DTO
 * @property name Display Name
 * @property imgSrc 아바타 URL
 * @property follower 팔로워 숫자
 * @property following 팔로잉 숫자
 */
data class UserInfo (
    val name: String,
    val imgSrc: String,
    val follower: Int,
    val following: Int
)