package com.nikki.news.ui.main




import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.nikki.news.R
import com.nikki.news.databinding.ActivityMainBinding
import com.nikki.news.state.NetworkState
import com.nikki.news.ui.DetailNewsActivity
import com.nikki.news.ui.adapter.NewsAdapters
import com.nikki.news.utils.Constants
import com.nikki.news.utils.Constants.Companion.QUERY_PER_PAGE
import com.nikki.news.utils.EndlessRecyclerOnScrollListener
import com.nikki.news.utils.EspressoIdlingResource
import com.nikki.news.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapters
    private val countryCode = Constants.CountryCode
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.tvToolbar  // Use the existing toolbar from the layout

        setSupportActionBar(toolbar) // Set the toolbar as the support action bar

        if (supportActionBar != null) {
            supportActionBar?.title = "News"
        }
        toolbar.inflateMenu(R.menu.main_menu)

        newsAdapter = NewsAdapters()
        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)
        tabView()
        getNewsByCategory("general")
        search()

    }

    private fun search(){
        binding.btnFilter.setOnClickListener {
            val query = binding.etFilter.text.toString().trim().replace("\\s+".toRegex(), "-")
                .lowercase(Locale.getDefault())
            mainViewModel.searchNews(query)
            collectSearchResponse()
            binding.rvNews.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)

            }
        }



    }

    private fun collectSearchResponse() {
        //Search response
        lifecycleScope.launchWhenStarted {
            if (mainViewModel.searchEnable) {
                mainViewModel.searchNewsResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {

                            response.data?.let { searchResponse ->
                                EspressoIdlingResource.decrement()
                                newsAdapter.differ.submitList(searchResponse.articles.toList())
                                mainViewModel.totalPage =
                                    searchResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel.searchNewsPage == mainViewModel.totalPage + 1

                            }
                        }

                        is NetworkState.Loading -> {

                        }

                        is NetworkState.Error -> {

                            response.message?.let {

                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }






    fun getNewsByCategory(category: String){
        mainViewModel.newsResponse.value.data?.articles?.clear()
        setupObservers()
        EspressoIdlingResource.increment()
        mainViewModel.fetchNewsByCategory(countryCode, category)
        newsAdapter = NewsAdapters()
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE){
            override fun onLoadMore() {
                mainViewModel.fetchNews(countryCode)
            }

        }

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)

        }

        newsAdapter.setOnItemClickListener {
            val intent = Intent(this@MainActivity, DetailNewsActivity::class.java)
            intent.putExtra("url", it.url)
            startActivity(intent)
        }

        newsAdapter.notifyDataSetChanged()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            if (!mainViewModel.searchEnable) {
                mainViewModel.newsResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            response.data?.let { newResponse ->
                                EspressoIdlingResource.decrement()
                                newsAdapter.differ.submitList(newResponse.articles.toList())
                                mainViewModel.totalPage =
                                    newResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel.feedNewsPage == mainViewModel.totalPage + 1
                            }
                        }

                        is NetworkState.Loading -> {
                            //showProgressBar()
                        }

                        is NetworkState.Error -> {
                            //hideProgressBar()
                            response.message?.let {
                               // showErrorMessage(response.message)
                            }
                        }

                        else -> {}
                    }
                }
            } else {
               // collectSearchResponse()
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
                }
                //mainViewModel.hideErrorToast()
            }
        }
    }



    fun tabView(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Business"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Entertainment"))
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
                        getNewsByCategory("general")
                    }
                    1 -> {
                        getNewsByCategory("business")
                    }
                    2 -> {
                        getNewsByCategory("entertainment")
                    }
                    3 -> {
                        getNewsByCategory("health")
                    }
                    4 -> {
                        getNewsByCategory("science")
                    }
                    5 -> {
                        getNewsByCategory("sports")
                    }
                    6 -> {
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logout -> {
                auth.signOut()
                sessionManager.logoutUser()
                Toast.makeText(this,"tes", Toast.LENGTH_SHORT).show()
                finish()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }


}