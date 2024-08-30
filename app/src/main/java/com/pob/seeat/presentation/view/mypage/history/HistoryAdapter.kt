package com.pob.seeat.presentation.view.mypage.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pob.seeat.databinding.ItemFeedSummaryBinding
import com.pob.seeat.databinding.ItemMessageTagBinding
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.presentation.view.mypage.items.HistoryEnum
import com.pob.seeat.presentation.view.mypage.items.HistoryListItem
import com.pob.seeat.utils.Utils.px

class HistoryAdapter : ListAdapter<HistoryListItem, ViewHolder<HistoryListItem>>(object :
    DiffUtil.ItemCallback<HistoryListItem>() {
    override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) =
        when {
            oldItem is HistoryListItem.FeedItem && newItem is HistoryListItem.FeedItem -> oldItem.uId == newItem.uId
            else -> false
        }

    override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) =
        oldItem == newItem
}) {
    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is HistoryListItem.FeedItem -> HistoryEnum.FEED.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<HistoryListItem> {
        val historyViewType = HistoryEnum.entries.find { it.viewType == viewType }
        return when (historyViewType) {
            HistoryEnum.FEED -> FeedViewHolder(
                ItemFeedSummaryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<HistoryListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    class FeedViewHolder(binding: ItemFeedSummaryBinding) :
        ViewHolder<HistoryListItem>(binding.root) {
        private val tagContainer = binding.llFeedTagContainer
        private val title = binding.tvFeedTitle
        private val content = binding.tvFeedContent
        private val commentCount = binding.tvFeedCommentCount
        private val likeCount = binding.tvFeedLikeCount
        private val time = binding.tvFeedTime

        override fun bind(item: HistoryListItem, onClick: (HistoryListItem) -> Unit) {
            (item as HistoryListItem.FeedItem).let { feed ->
                title.text = feed.title
                content.text = feed.content
                commentCount.text = feed.commentCount.toString()
                likeCount.text = feed.likeCount.toString()
                time.text = feed.time

                feed.tagList.forEach { tagName ->
                    tagContainer.addView(
                        ItemMessageTagBinding
                            .inflate(LayoutInflater.from(itemView.context))
                            .apply {
                                tvTagName.text = tagName
                                root.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, 0, 8f.px, 0)
                                }
                            }
                            .root
                    )
                }
            }
        }
    }
}