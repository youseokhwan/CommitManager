package com.youseokhwan.commitmanager.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * RetrofitService
 */
interface RetrofitService {

    /**
     * GitHub ID가 유효한 ID인지 확인
     */
//    @Headers("")
    @GET("/user")
    fun idCheck(
        @Query("id") id: String
    ): Call<User>

    /**
     *
     */
//    @Headers("")
    @GET("/userinfo")
    fun getUserInfo(
        @Query("id") id: String
    ): Call<UserInfo>

    /**
     * 오늘자 커밋 여부 확인
     */
    //    @Headers("")
    @GET("/commit")
    fun getTodayCommit(
        @Query("id") id: String
    ): Call<Commit>

}