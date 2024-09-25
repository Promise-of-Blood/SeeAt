package com.pob.seeat.presentation.view.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pob.seeat.databinding.ItemAdminCommentReportBinding
import com.pob.seeat.databinding.ItemAdminFeedReportBinding
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.presentation.view.admin.items.AdminEnum
import com.pob.seeat.presentation.view.admin.items.AdminReportListItem

class AdminReportRecyclerViewAdapter(
    private val onClick: (AdminReportListItem) -> Unit = {},
    private val handleEmptyList: (Int) -> Unit = {},
) : ListAdapter<AdminReportListItem, ViewHolder<AdminReportListItem>>(object :
    DiffUtil.ItemCallback<AdminReportListItem>() {
    override fun areItemsTheSame(oldItem: AdminReportListItem, newItem: AdminReportListItem) =
        when {
            oldItem is AdminReportListItem.FeedReport && newItem is AdminReportListItem.FeedReport -> oldItem.content == newItem.content
            oldItem is AdminReportListItem.CommentReport && newItem is AdminReportListItem.CommentReport -> oldItem.content == newItem.content
            else -> false
        }

    override fun areContentsTheSame(oldItem: AdminReportListItem, newItem: AdminReportListItem) =
        when {
            oldItem is AdminReportListItem.FeedReport && newItem is AdminReportListItem.FeedReport -> oldItem == newItem
            oldItem is AdminReportListItem.CommentReport && newItem is AdminReportListItem.CommentReport -> oldItem == newItem
            else -> false
        }
}), Filterable {

    private var originalList: List<AdminReportListItem> = emptyList()

    fun setOriginalList(list: List<AdminReportListItem>) {
        originalList = list
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is AdminReportListItem.FeedReport -> AdminEnum.FEED_REPORT.viewType
        is AdminReportListItem.CommentReport -> AdminEnum.COMMENT_REPORT.viewType
        else -> AdminEnum.FEED_REPORT.viewType
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder<AdminReportListItem> = when (viewType) {
        AdminEnum.FEED_REPORT.viewType -> FeedReportViewHolder(
            ItemAdminFeedReportBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ),
        )

        AdminEnum.COMMENT_REPORT.viewType -> CommentReportViewHolder(
            ItemAdminCommentReportBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ),
        )

        else -> FeedReportViewHolder(
            ItemAdminFeedReportBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<AdminReportListItem>, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class FeedReportViewHolder(binding: ItemAdminFeedReportBinding) :
        ViewHolder<AdminReportListItem>(binding.root) {
        private val title = binding.tvAdminTitle
        private val content = binding.tvAdminContent
        private val reportCount = binding.tvAdminReport
        private val more = binding.ibMore
        private val menu = binding.llAdminMenu

        override fun bind(item: AdminReportListItem, onClick: (AdminReportListItem) -> Unit) {
            (item as AdminReportListItem.FeedReport).let {
                title.text = item.content?.title ?: ""
                content.text = item.content?.content ?: ""
                reportCount.text = item.reportedCount.toString()
                menu.visibility = View.GONE
                more.visibility = View.GONE
            }
        }
    }

    class CommentReportViewHolder(binding: ItemAdminCommentReportBinding) :
        ViewHolder<AdminReportListItem>(binding.root) {
        private val content = binding.tvAdminContent
        private val reportCount = binding.tvAdminReport
        private val more = binding.ibMore
        private val menu = binding.llAdminMenu

        override fun bind(item: AdminReportListItem, onClick: (AdminReportListItem) -> Unit) {
            (item as AdminReportListItem.CommentReport).let {
                content.text = item.content?.comment ?: ""
                reportCount.text = item.reportedCount.toString()
                menu.visibility = View.GONE
                more.visibility = View.GONE
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
                    handleEmptyList(originalList.size)
                    submitList(originalList)
                } else {
                    handleEmptyList((results.values as? List<AdminReportListItem>)?.size ?: 0)
                    submitList(results.values as? List<AdminReportListItem>)
                }
            }
        }
    }

    private fun filterByOption(target: String, list: List<AdminReportListItem>) = when (target) {
        "댓글" -> list.filterIsInstance<AdminReportListItem.CommentReport>()
        "게시글" -> list.filterIsInstance<AdminReportListItem.FeedReport>()
        else -> list
    }
}