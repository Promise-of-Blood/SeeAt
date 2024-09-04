package com.pob.seeat.presentation.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pob.seeat.databinding.ItemDetailImagesBinding

class ImagesPagerAdapter(private var imageList: List<String>) :
    RecyclerView.Adapter<ImagesPagerAdapter.PagerViewHolder>() {

    class PagerViewHolder(val binding: ItemDetailImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding =
            ItemDetailImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageList[position])
            .into(holder.binding.ivFeedMainImage)
    }
}