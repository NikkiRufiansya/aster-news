
package com.nikki.news.network.api

import com.nikki.news.data.model.NewsResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNewsByCategory(countryCode: String, category: String): Response<NewsResponse>
}