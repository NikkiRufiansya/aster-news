

package com.nikki.news.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikki.news.data.model.NewsArticle
import com.nikki.news.data.model.NewsResponse
import com.nikki.news.di.CoroutinesDispatcherProvider
import com.nikki.news.network.repository.INewsRepository
import com.nikki.news.state.NetworkState
import com.nikki.news.utils.Constants
import com.nikki.news.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: INewsRepository,
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {


    private val TAG = "MainViewModel"
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String>
        get() = _errorMessage

    private val _newsResponse = MutableStateFlow<NetworkState<NewsResponse>>(NetworkState.Empty())
    val newsResponse: StateFlow<NetworkState<NewsResponse>>
        get() = _newsResponse

    private val _searchNewsResponse =
        MutableStateFlow<NetworkState<NewsResponse>>(NetworkState.Empty())
    val searchNewsResponse: StateFlow<NetworkState<NewsResponse>>
        get() = _searchNewsResponse

    private var feedResponse: NewsResponse? = null
    var feedNewsPage = 1

    var searchEnable: Boolean = false
    var searchNewsPage = 1
    private var searchResponse: NewsResponse? = null
    private var oldQuery: String = ""
    var newQuery: String = ""
    var totalPage = 1

    init {
        fetchNews(Constants.CountryCode)
    }

    fun fetchNews(countryCode: String) {

        if (feedNewsPage <= totalPage) {
            if (networkHelper.isNetworkConnected()) {
                viewModelScope.launch(coroutinesDispatcherProvider.io) {
                    _newsResponse.value = NetworkState.Loading()
                    when (val response = repository.getNews(countryCode, feedNewsPage)) {
                        is NetworkState.Success -> {
                            _newsResponse.value = handleFeedNewsResponse(response)
                        }
                        is NetworkState.Error -> {
                            _newsResponse.value =
                                NetworkState.Error(
                                    response.message ?: "Error"
                                )
                        }
                        else -> {}
                    }

                }
            } else {
                _errorMessage.value = "No internet available"
            }
        }
    }

    fun fetchNewsByCategory(countryCode: String, category: String){

        if(networkHelper.isNetworkConnected()){
            viewModelScope.launch(coroutinesDispatcherProvider.io){
                _newsResponse.value = NetworkState.Loading()
                when (val response = repository.getNewsByCategory(countryCode, category)){
                    is NetworkState.Success -> {
                        _newsResponse.value = handleFeedNewsByCategoryResponse(response)
                    }
                    is NetworkState.Error -> {
                        _newsResponse.value = NetworkState.Error(response.message ?: "Error")
                    }
                    else -> {}
                }
            }
        }else{
            _errorMessage.value = "No internet available"
        }
    }

    private fun handleFeedNewsResponse(response: NetworkState<NewsResponse>): NetworkState<NewsResponse> {
        response.data?.let { resultResponse ->
            if (feedResponse == null) {
                feedNewsPage = 2
                feedResponse = resultResponse
            } else {
                feedNewsPage++
                val oldArticles = feedResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            //Conversion
            feedResponse?.let {
                //convertPublishedDate(it)
            }

            return NetworkState.Success(feedResponse ?: resultResponse)


        }
        return NetworkState.Error("No data found")
    }

    private fun handleFeedNewsByCategoryResponse(response: NetworkState<NewsResponse>): NetworkState<NewsResponse> {
        response.data?.let { resultResponse ->
            if (feedResponse == null) {
                feedNewsPage = 2
                feedResponse = resultResponse
            } else {
                feedNewsPage++
                val oldArticles = feedResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            //Conversion
            feedResponse?.let {
                //convertPublishedDate(it)
            }

            return NetworkState.Success(feedResponse ?: resultResponse)


        }
        return NetworkState.Error("No data found")
    }

    fun searchNews(query: String) {
        newQuery = query
        if (newQuery.isNotEmpty() && searchNewsPage <= totalPage) {
            if (networkHelper.isNetworkConnected()) {
                viewModelScope.launch(coroutinesDispatcherProvider.io) {
                    _searchNewsResponse.value = NetworkState.Loading()
                    when (val response = repository.searchNews(query, searchNewsPage)) {
                        is NetworkState.Success -> {
                            _searchNewsResponse.value = handleSearchNewsResponse(response)
                        }
                        is NetworkState.Error -> {
                            _searchNewsResponse.value =
                                NetworkState.Error(
                                    response.message ?: "Error"
                                )
                        }
                        else -> {}
                    }
                }
            } else {
                _errorMessage.value = "No internet available"
            }
        }
    }

    private fun handleSearchNewsResponse(response: NetworkState<NewsResponse>): NetworkState<NewsResponse> {
        response.data?.let { resultResponse ->
            if (searchResponse == null || oldQuery != newQuery) {
                searchNewsPage = 2
                searchResponse = resultResponse
                oldQuery = newQuery
            } else {
                searchNewsPage++
                val oldArticles = searchResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            searchResponse?.let {

            }
            return NetworkState.Success(searchResponse ?: resultResponse)
        }
        return NetworkState.Error("No data found")
    }





    fun hideErrorToast() {
        _errorMessage.value = ""
    }

    fun saveNews(news: NewsArticle) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutinesDispatcherProvider.io + coroutineExceptionHandler) {
            repository.saveNews(news)
        }
    }


    private fun onError(throwable: Throwable) {
        throwable.message?.let {
            _errorMessage.value = it
        }
    }
}