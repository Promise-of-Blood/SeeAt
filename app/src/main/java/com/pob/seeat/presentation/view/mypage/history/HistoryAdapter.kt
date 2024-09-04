package com.pob.seeat.presentation.view.mypage.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pob.seeat.databinding.ItemCommentHistoryBinding
import com.pob.seeat.databinding.ItemFeedHistoryBinding
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.presentation.view.mypage.items.HistoryEnum
import com.pob.seeat.presentation.view.mypage.items.HistoryListItem
import com.pob.seeat.utils.Utils.toFormatShortenedString

class HistoryAdapter : ListAdapter<HistoryListItem, ViewHolder<HistoryListItem>>(object :
    DiffUtil.ItemCallback<HistoryListItem>() {
    override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) =
        when {
            oldItem is HistoryListItem.FeedItem && newItem is HistoryListItem.FeedItem -> oldItem.feedId == newItem.feedId
            oldItem is HistoryListItem.CommentItem && newItem is HistoryListItem.CommentItem -> oldItem.commentId == newItem.commentId
            else -> false
        }

    override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) =
        oldItem == newItem
}) {
    override fun getItemViewType(position: Int) = when (val item = getItem(position)) {
        is HistoryListItem.FeedItem -> HistoryEnum.FEED.viewType
        is HistoryListItem.CommentItem -> HistoryEnum.COMMENT.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<HistoryListItem> {
        val historyViewType = HistoryEnum.entries.find { it.viewType == viewType }
        return when (historyViewType) {
            HistoryEnum.FEED -> FeedViewHolder(
                ItemFeedHistoryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> CommentViewHolder(
                ItemCommentHistoryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<HistoryListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    class FeedViewHolder(binding: ItemFeedHistoryBinding) :
        ViewHolder<HistoryListItem>(binding.root) {
        private val title = binding.tvFeedTitle
        private val content = binding.tvFeedContent
        private val commentCount = binding.tvFeedCommentCount
        private val likeCount = binding.tvFeedLikeCount
        private val time = binding.tvFeedTime
        private val image = binding.ivFeedImage

        override fun bind(item: HistoryListItem, onClick: (HistoryListItem) -> Unit) {
            (item as HistoryListItem.FeedItem).let { feed ->
                title.text = feed.title
                content.text = feed.content
                commentCount.text = feed.commentCount.toFormatShortenedString()
                likeCount.text = feed.likeCount.toFormatShortenedString()
                time.text = feed.time
                Glide.with(itemView.context)
                    .load(feed.image)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                    .into(image)
            }
        }
    }

    class CommentViewHolder(binding: ItemCommentHistoryBinding) :
        ViewHolder<HistoryListItem>(binding.root) {
        private val feedTitle = binding.tvCommentPostTitle
        private val comment = binding.tvCommentContent
        private val time = binding.tvCommentTime

        override fun bind(item: HistoryListItem, onClick: (HistoryListItem) -> Unit) {
            (item as HistoryListItem.CommentItem).let {
                comment.text = it.comment
                time.text = it.time
                feedTitle.text = it.feedTitle
            }
            itemView.setOnClickListener { onClick(item) }
        }
    }
}