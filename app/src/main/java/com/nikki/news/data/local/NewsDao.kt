
package com.nikki.news.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nikki.news.data.model.NewsArticle



@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticle: NewsArticle): Long

    @Query("SELECT * FROM news_articles")
    fun getAllNews(): LiveData<List<NewsArticle>>

    @Delete
    suspend fun deleteNews(newsArticle: NewsArticle)

    @Query("Delete FROM news_articles")
    suspend fun deleteAllNews()
}