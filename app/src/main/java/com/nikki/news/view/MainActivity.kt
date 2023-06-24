package com.nikki.news.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.nikki.news.adapter.NewsAdapters
import com.nikki.news.core.helper.Connection
import com.nikki.news.core.service.NewsService
import com.nikki.news.databinding.ActivityMainBinding
import com.nikki.news.model.NewsModels
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var connection: Connection
    lateinit var newsService: NewsService
    var newsModels : ArrayList<NewsModels> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connection = Connection(this)
        newsService = NewsService.create(this)

        getAllNews()
        tabView()

        binding.btnFilter.setOnClickListener {
            val query: String =
                binding.etFilter.text.toString().trim().replace("\\s+", "-").toLowerCase()
            searchNews(query)
        }

    }



    fun tabView(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Business"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Entertainment"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("General"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Health"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Science"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Sports"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Technology"))

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
        binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        getAllNews()
                    }
                    1 -> {
                        getNewsByCategory("business")
                    }
                    2 -> {
                        getNewsByCategory("entertainment")
                    }
                    3 -> {
                        getNewsByCategory("general")
                    }
                    4 -> {
                        getNewsByCategory("health")
                    }
                    5 -> {
                        getNewsByCategory("science")
                    }
                    6 -> {
                        getNewsByCategory("sports")
                    }
                    7 -> {
                        getNewsByCategory("technology")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun getAllNews(){
        binding.etFilter.setText("")
        newsModels.clear()
        if (connection.isConnectingToInternet()){
            var news = newsService.getAll()
            news.enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        var responBody = response.body()?.string()
                        var jsonObject = JSONObject(responBody)
                        var articles = jsonObject.getJSONArray("articles")
                        for (i in 0 until articles.length()){
                            var item = articles.getJSONObject(i)
                            var data = NewsModels()
                            if (item.has("title")) data.title = item.getString("title")
                            if (item.has("content")) data.content = item.getString("content")
                            if (item.has("urlToImage")) data.urlToImage = item.getString("urlToImage")
                            if (item.has("url")) data.url = item.getString("url")
                            newsModels.add(data)
                        }

                        var layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
                        binding.rvNews.layoutManager = layoutManager
                        val newsAdapters : NewsAdapters = NewsAdapters(newsModels)
                        newsAdapters.setOnClick {
                            val intent = Intent(this@MainActivity, DetailNewsActivity::class.java)
                            intent.putExtra("url", it.url)
                            startActivity(intent)
                        }
                        binding.rvNews.adapter = newsAdapters
                        newsAdapters.notifyDataSetChanged()

                    }else{
                        var responseError = response.errorBody()?.string()
                        var jsonObject = JSONObject(responseError)
                        Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    fun getNewsByCategory(category: String){
        binding.etFilter.setText("")
        newsModels.clear()
        if (connection.isConnectingToInternet()){
            var news = newsService.getNewsByCategory(category)
            news.enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        var responBody = response.body()?.string()
                        var jsonObject = JSONObject(responBody)
                        var articles = jsonObject.getJSONArray("articles")
                        for (i in 0 until articles.length()){
                            var item = articles.getJSONObject(i)
                            var data = NewsModels()
                            if (item.has("title")) data.title = item.getString("title")
                            if (item.has("content")) data.content = item.getString("content")
                            if (item.has("urlToImage")) data.urlToImage = item.getString("urlToImage")
                            if (item.has("url")) data.url = item.getString("url")
                            newsModels.add(data)
                        }

                        var layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
                        binding.rvNews.layoutManager = layoutManager
                        val newsAdapters : NewsAdapters = NewsAdapters(newsModels)
                        newsAdapters.setOnClick {
                            val intent = Intent(this@MainActivity, DetailNewsActivity::class.java)
                            intent.putExtra("url", it.url)
                            startActivity(intent)
                        }
                        binding.rvNews.adapter = newsAdapters
                        newsAdapters.notifyDataSetChanged()

                    }else{
                        var responseError = response.errorBody()?.string()
                        var jsonObject = JSONObject(responseError)
                        Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }


    }

    fun searchNews(query : String){
        newsModels.clear()
        if (connection.isConnectingToInternet()){
            var news = newsService.filterBySources(query)
            news.enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        var responBody = response.body()?.string()
                        var jsonObject = JSONObject(responBody)
                        var articles = jsonObject.getJSONArray("articles")
                        Log.d("TAG", "onResponse: " + articles)
                        for (i in 0 until articles.length()){
                            var item = articles.getJSONObject(i)
                            var data = NewsModels()
                            if (item.has("title")) data.title = item.getString("title")
                            if (item.has("content")) data.content = item.getString("content")
                            if (item.has("urlToImage")) data.urlToImage = item.getString("urlToImage")
                            if (item.has("url")) data.url = item.getString("url")
                            newsModels.add(data)
                        }

                        var layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
                        binding.rvNews.layoutManager = layoutManager
                        val newsAdapters : NewsAdapters = NewsAdapters(newsModels)
                        newsAdapters.setOnClick {
                            val intent = Intent(this@MainActivity, DetailNewsActivity::class.java)
                            intent.putExtra("url", it.url)
                            startActivity(intent)
                        }
                        binding.rvNews.adapter = newsAdapters
                        newsAdapters.notifyDataSetChanged()

                    }else{
                        var responseError = response.errorBody()?.string()
                        var jsonObject = JSONObject(responseError)
                        Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
}