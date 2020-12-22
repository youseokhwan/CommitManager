package com.youseokhwan.commitmanager.realm

import io.realm.RealmObject

/**
 * GitHub API를 통해 받아온 유저 정보를 저장하는 모델
 */
open class User(
    var id       : String = "",  // GitHub 아이디
    var token    : String = "",  // OAuth 토큰
    var name     : String = "",  // Display Name
    var imgSrc   : String = "",  // GitHub Avatar 이미지 주소
    var follower : Int    = 0,   // 팔로워 수
    var following: Int    = 0    // 팔로잉 수
) : RealmObject()
