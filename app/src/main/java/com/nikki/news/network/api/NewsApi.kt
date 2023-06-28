

package com.nikki.news.network.api

import com.nikki.news.data.model.NewsResponse
import com.nikki.news.utils.Constants.Companion.API_KEY
import com.nikki.news.utils.Constants.Companion.CountryCode
import com.nikki.news.utils.Constants.Companion.QUERY_PER_PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = CountryCode,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/top-headlines")
    suspend fun getNewsByCategory(
        @Query("country")
        countryCode: String = CountryCode,
        @Query("category") category: String,
        @Query("apiKey")
        apiKey: String = API_KEY) : Response<NewsResponse>

}