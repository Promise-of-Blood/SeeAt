package com.pob.seeat.presentation.view.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemFeedBookmarkBinding
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toTagList

class BookmarkAdapter(private val onClick: (BookmarkModel) -> Unit) :
    ListAdapter<BookmarkModel, ViewHolder<BookmarkModel>>(object :
        DiffUtil.ItemCallback<BookmarkModel>() {
        override fun areItemsTheSame(oldItem: BookmarkModel, newItem: BookmarkModel): Boolean {
            return oldItem.feedId == newItem.feedId
        }

        override fun areContentsTheSame(oldItem: BookmarkModel, newItem: BookmarkModel): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<BookmarkModel> {
        return Holder(
            ItemFeedBookmarkBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<BookmarkModel>, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class Holder(binding: ItemFeedBookmarkBinding) : ViewHolder<BookmarkModel>(binding.root) {
        private val container = binding.container
        private val title = binding.tvFeedTitle
        private val content = binding.tvFeedContent
        private val commentCount = binding.tvFeedCommentCount
        private val likeCount = binding.tvFeedLikeCount
        private val time = binding.tvFeedTime
        private val nickname = binding.tvFeedNickname
        private val image = binding.ivFeedImage
        private val chipGroup = binding.cgFeedTag

        override fun bind(item: BookmarkModel, onClick: (BookmarkModel) -> Unit) {
            title.text = item.title
            content.text = item.content
            commentCount.text = if (item.like > 100) "99+" else item.commentsCount.toString()
            likeCount.text = if (item.like > 100) "99+" else item.like.toString()
            time.text = item.date.toKoreanDiffString()
            nickname.text = item.nickname
            itemView.setOnClickListener { onClick(item) }
            Glide.with(itemView.context).load(item.contentImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10))).into(image)
            if (item.tags.isEmpty()) {
                chipGroup.visibility = View.GONE
                container.setPadding(16f.px, 12f.px, 16f.px, 12f.px)
            } else {
                chipGroup.addFeedTags(item.tags)
                container.setPadding(16f.px, 0f.px, 16f.px, 12f.px)
            }
        }

        private fun ChipGroup.addFeedTags(tags: List<String>) {
            this.removeAllViews()
            for (tag in tags.toTagList()) {
                val chip = Chip(context).apply {
                    text = tag.tagName
                    textSize = 12f
                    textEndPadding = 4f.px.toFloat()
                    setTextColor(AppCompatResources.getColorStateList(context, R.color.black))

                    setChipIconResource(tag.tagImage)
                    chipBackgroundColor =
                        AppCompatResources.getColorStateList(context, R.color.white)
                    chipStrokeWidth = 0f
                    chipIconSize = 12f.px.toFloat()
                    chipMinHeight = 24f.px.toFloat()
                    chipCornerRadius = 32f.px.toFloat()
                    iconStartPadding = 4f.px.toFloat()

                    elevation = 3f.px.toFloat()
                    rippleColor = null
                    isCheckable = false
                    isClickable = false
                }
                this.addView(chip)
            }
        }
    }
}