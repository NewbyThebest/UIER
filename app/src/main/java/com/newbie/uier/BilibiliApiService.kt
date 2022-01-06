package com.newbie.uier

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BilibiliApiService {

    @GET("x/web-interface/view")
    suspend fun getVideoList(
        @Query("bvid") bvid: String
    ): BaseData<BvidBean>

    @GET("x/player/playurl")
    suspend fun getVideo(
        @Query("cid") cid: String,
        @Query("bvid") bvid: String
    ): BaseData<VideoBean>

    companion object{
        private const val BASE_URL = "https://api.bilibili.com/"
        private var service: BilibiliApiService? = null

        fun getApiService(): BilibiliApiService{
            if (service == null){
                var httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }

                var client = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .build()

                var retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                service = retrofit.create(BilibiliApiService::class.java)
            }
            return service!!
        }
    }
}