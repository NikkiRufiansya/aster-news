
package com.nikki.news.network.repository

import androidx.lifecycle.LiveData
import com.nikki.news.data.model.NewsArticle
import com.nikki.news.data.model.NewsResponse
import com.nikki.news.state.NetworkState

interface INewsRepository {
    suspend fun getNews(countryCode: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun getNewsByCategory(countryCode: String, category: String): NetworkState<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun saveNews(news: NewsArticle): Long

    fun getSavedNews(): LiveData<List<NewsArticle>>

    suspend fun deleteNews(news: NewsArticle)

    suspend fun deleteAllNews()
}
