package com.example.petmedical.map

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/local/search/keyword.json")    // Keyword.json 의 정보를 받아옴

    // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김
    fun getSearchKeyword(
        @Header("Authorization") key: String,  // 카카오 API 인증키
        @Query("query") query: String,         // 검색을 원하는 질의어
        @Query("x") x: Double?,                 // 경도
        @Query("y") y: Double?,                  // 위도
        @Query("radius") radius: Int,
        @Query("sort") sort: String
    ): Call<ResultSearchKeyword>
}