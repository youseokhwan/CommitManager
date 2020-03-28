package com.youseokhwan.commitmanager.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * UserRetrofit
 */
object UserRetrofit {
    fun getService(): RetrofitService = retrofit.create(RetrofitService::class.java)

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-18-223-112-230.us-east-2.compute.amazonaws.com:3001")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}