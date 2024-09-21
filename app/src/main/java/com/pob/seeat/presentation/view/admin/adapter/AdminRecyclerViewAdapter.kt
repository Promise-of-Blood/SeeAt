package com.pob.seeat.presentation.view.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.pob.seeat.databinding.ItemAdminCommentReportBinding
import com.pob.seeat.databinding.ItemAdminFeedReportBinding
import com.pob.seeat.databinding.ItemAdminUserBinding
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.presentation.view.admin.items.AdminEnum
import com.pob.seeat.presentation.view.admin.items.AdminListItem

class AdminRecyclerViewAdapter(private val onClick: (AdminListItem) -> Unit = {}) :
    ListAdapter<AdminListItem, ViewHolder<AdminListItem>>(object :
        DiffUtil.ItemCallback<AdminListItem>() {
        override fun areItemsTheSame(oldItem: AdminListItem, newItem: AdminListItem) = when {
            oldItem is AdminListItem.User && newItem is AdminListItem.User -> oldItem.uid == newItem.uid
            oldItem is AdminListItem.FeedReport && newItem is AdminListItem.FeedReport -> oldItem.feedId == newItem.feedId
            oldItem is AdminListItem.CommentReport && newItem is AdminListItem.CommentReport -> oldItem.commentId == newItem.commentId
            else -> false
        }

        override fun areContentsTheSame(oldItem: AdminListItem, newItem: AdminListItem) = when {
            oldItem is AdminListItem.User && newItem is AdminListItem.User -> oldItem == newItem
            oldItem is AdminListItem.FeedReport && newItem is AdminListItem.FeedReport -> oldItem == newItem
            oldItem is AdminListItem.CommentReport && newItem is AdminListItem.CommentReport -> oldItem == newItem
            else -> false
        }
    }), Filterable {

    private var originalList: List<AdminListItem> = emptyList()

    fun setOriginalList(list: List<AdminListItem>) {
        originalList = list
    }

    override fun submitList(list: List<AdminListItem>?) {
        super.submitList(list)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is AdminListItem.User -> AdminEnum.USER.viewType
        is AdminListItem.FeedReport -> AdminEnum.FEED_REPORT.viewType
        is AdminListItem.CommentReport -> AdminEnum.COMMENT_REPORT.viewType
        else -> AdminEnum.USER.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<AdminListItem> {
        val adminViewType = AdminEnum.entries.find { it.viewType == viewType }
        return when (adminViewType) {
            AdminEnum.USER -> UserViewHolder(
                ItemAdminUserBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            AdminEnum.FEED_REPORT -> FeedViewHolder(
                ItemAdminFeedReportBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            AdminEnum.COMMENT_REPORT -> CommentViewHolder(
                ItemAdminCommentReportBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> UserViewHolder(
                ItemAdminUserBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<AdminListItem>, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class UserViewHolder(binding: ItemAdminUserBinding) : ViewHolder<AdminListItem>(binding.root) {
        private val image = binding.ivAdminUserImage
        private val nickname = binding.tvAdminUserNickname
        private val email = binding.tvAdminUserEmail
        private val adminIcon = binding.ivAdminUserBadge

        override fun bind(item: AdminListItem, onClick: (AdminListItem) -> Unit) {
            (item as AdminListItem.User).let {
                nickname.text = item.nickname
                email.text = item.email
                adminIcon.visibility = if (item.isAdmin) View.VISIBLE else View.GONE
                Glide.with(itemView.context).load(item.profileImage)
                    .transform(CenterCrop(), RoundedCorners(100)).into(image)
                itemView.setOnClickListener { onClick(item) }
            }
        }
    }

    class FeedViewHolder(binding: ItemAdminFeedReportBinding) :
        ViewHolder<AdminListItem>(binding.root) {
        private val title = binding.tvAdminTitle
        private val content = binding.tvAdminContent
        private val reportCount = binding.tvAdminReport

        override fun bind(item: AdminListItem, onClick: (AdminListItem) -> Unit) {
            (item as AdminListItem.FeedReport).let {
                title.text = item.feedTitle
                content.text = item.feedContent
                reportCount.text = item.reportCount.toString()
                itemView.setOnClickListener { onClick(item) }
            }
        }
    }

    class CommentViewHolder(binding: ItemAdminCommentReportBinding) :
        ViewHolder<AdminListItem>(binding.root) {
        private val content = binding.tvAdminContent
        private val reportCount = binding.tvAdminReport

        override fun bind(item: AdminListItem, onClick: (AdminListItem) -> Unit) {
            (item as AdminListItem.CommentReport).let {
                content.text = item.comment
                reportCount.text = item.reportCount.toString()
                itemView.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().trim()
                return FilterResults().apply {
                    values = filterByOption(charString, originalList)
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (constraint.isBlank()) {
                    submitList(originalList)
                } else {
                    submitList(results.values as? List<AdminListItem>)
                }
            }
        }
    }

    private fun filterByOption(target: String, list: List<AdminListItem>) = when (target) {
        "댓글" -> list.filterIsInstance<AdminListItem.CommentReport>()
        "게시글" -> list.filterIsInstance<AdminListItem.FeedReport>()
        else -> list
    }

    fun sortByOption(option: String) {
        when (option) {
            "신고순" -> submitList(originalList.sortedByDescending {
                if (it is AdminListItem.CommentReport) it.reportCount else if (it is AdminListItem.FeedReport) it.reportCount else it.hashCode()
            })

            else -> submitList(originalList)
        }
    }
}