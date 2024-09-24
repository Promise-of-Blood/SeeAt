package com.pob.seeat.presentation.view.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pob.seeat.databinding.ItemAdminFeedReportBinding
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.presentation.view.admin.items.AdminEnum
import com.pob.seeat.presentation.view.admin.items.AdminReportListItem

class AdminReportRecyclerViewAdapter(
    private val onClick: (AdminReportListItem) -> Unit = {}
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

        AdminEnum.COMMENT_REPORT.viewType -> FeedReportViewHolder(
            ItemAdminFeedReportBinding.inflate(
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
        private val navigateButton = binding.btnReportDetailNavigate
        private val ignoreButton = binding.btnReportDetailIgnore
        private val deleteButton = binding.btnReportDetailDelete

        override fun bind(item: AdminReportListItem, onClick: (AdminReportListItem) -> Unit) {
            (item as AdminReportListItem.FeedReport).let {
                title.text = "제목"
                content.text = "내용"
                reportCount.text = "123"
                menu.visibility = View.GONE
            }
        }
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}