package com.nikki.news.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nikki.news.data.model.NewsArticle
import com.nikki.news.databinding.LayoutNewsBinding

import com.squareup.picasso.Picasso

class NewsAdapters  : RecyclerView.Adapter<NewsAdapters.ViewHolder>() {
    inner class ViewHolder(val binding: LayoutNewsBinding) : RecyclerView.ViewHolder(binding.root)


    private val differCallback = object : DiffUtil.ItemCallback<NewsArticle>(){
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((NewsArticle) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder){
            Picasso.get().load(article.urlToImage).into(binding.ivThumnail)
            binding.tvTitle.text = article.title
            binding.tvContent.text = article.content
        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }
    fun setOnItemClickListener(listener: (NewsArticle) -> Unit) {
        onItemClickListener = listener
    }
}