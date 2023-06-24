package com.nikki.news.core.service

import android.app.Activity
import com.nikki.news.core.retrofit.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsService {

    @GET("top-headlines?country=us&apiKey=ef9b61a3cade48aba7c1f2598a43ca90")
    fun getAll() : Call<ResponseBody>

    @GET("top-headlines?country=us&apiKey=ef9b61a3cade48aba7c1f2598a43ca90")
    fun getNewsByCategory(@Query("category") category: String) : Call<ResponseBody>

    @GET("top-headlines?apiKey=ef9b61a3cade48aba7c1f2598a43ca90")
    fun filterBySources(@Query("sources") source: String): Call<ResponseBody>
    companion object {
        fun create(activity: Activity): NewsService {
            return RetrofitClient.getClient(activity).create(NewsService::class.java)
        }
    }
}