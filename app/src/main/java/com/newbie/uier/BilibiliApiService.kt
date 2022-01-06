package com.newbie.uier

import androidx.databinding.Observable
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BilibiliApiService {

    @GET("x/web-interface/view")
    suspend fun getVideo(
        @Query("bvid") bvid: String
    ): BaseData<BvidBean>

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