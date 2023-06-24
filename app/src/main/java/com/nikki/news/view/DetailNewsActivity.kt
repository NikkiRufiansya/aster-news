package com.nikki.news.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.nikki.news.R
import com.nikki.news.databinding.ActivityDetailNewsBinding


class DetailNewsActivity : AppCompatActivity() {
    lateinit var binding : ActivityDetailNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var url = intent.getStringExtra("url").toString()

        binding.webView.webViewClient = WebViewClient()

        binding.webView.loadUrl(url)

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.settings.setSupportZoom(true)
    }


    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
           finish()

    }
}