
package com.nikki.news.network.api

import com.nikki.news.data.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val newsApi: NewsApi) : ApiHelper {

    override suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse> =
        newsApi.getNews(countryCode, pageNumber)

    override suspend fun getNewsByCategory(countryCode: String, category: String) : Response<NewsResponse> =
        newsApi.getNewsByCategory(countryCode, category)

    override suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse> =
        newsApi.searchNews(query, pageNumber)

}