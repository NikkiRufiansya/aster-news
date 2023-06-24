package com.nikki.news.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikki.news.databinding.LayoutNewsBinding
import com.nikki.news.model.NewsModels
import com.squareup.picasso.Picasso

class NewsAdapters (private var newsModels: List<NewsModels>) : RecyclerView.Adapter<NewsAdapters.ViewHolder>() {
    inner class ViewHolder(val binding: LayoutNewsBinding) : RecyclerView.ViewHolder(binding.root)

    private var onClick: ((NewsModels) -> Unit)? = null
    fun setOnClick(callback: (NewsModels) -> Unit){
        this.onClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsModels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(newsModels[position]){
                binding.tvTitle.text = this.title
                binding.tvContent.text = this.content
                Picasso.get().load(this.urlToImage).into(binding.ivThumnail)
                binding.cvNews.setOnClickListener {
                    onClick?.invoke(newsModels[position])
                }
            }
        }
    }
}