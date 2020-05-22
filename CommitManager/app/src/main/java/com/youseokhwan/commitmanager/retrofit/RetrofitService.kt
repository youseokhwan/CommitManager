package com.youseokhwan.commitmanager.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * RetrofitService
 */
interface RetrofitService {

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
        @Query("id") id: String,
        @Query("token") token: String
    ): Call<Commit>

}