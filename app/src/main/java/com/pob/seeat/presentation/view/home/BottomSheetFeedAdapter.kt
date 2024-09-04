package com.pob.seeat.presentation.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.pob.seeat.databinding.PostItemBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.common.ViewHolder
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

class BottomSheetFeedAdapter(private val onClick: (FeedModel) -> Unit) :
    ListAdapter<FeedModel, ViewHolder<FeedModel>>(object : DiffUtil.ItemCallback<FeedModel>() {
        override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem.feedId == newItem.feedId
        }

        override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem == newItem
        }


    }) {
    class PostViewHolder(val binding: PostItemBinding, private val onClick: (FeedModel) -> Unit) :
        ViewHolder<FeedModel>(binding.root) {
        override fun onBind(item: FeedModel) = with(binding) {

            if (item.contentImage.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.contentImage[0])
                    .into(ivPostMainImage)
            }

            tvPostTitle.text = item.title
            tvPostContent.text = item.content
            tvPostCommentCount.text = item.commentsCount.toString()
            tvPostLikeCount.text = item.like.toString()
            tvPostTimeAgo.text = item.date?.toLocalDateTime()?.toKoreanDiffString()
            tvPostUsername.text = item.nickname
            clFeedLayout.setOnClickListener {
                onClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<FeedModel> {
        return PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<FeedModel>, position: Int) {
        holder.onBind(getItem(position))
    }

}

