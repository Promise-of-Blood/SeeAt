package com.pob.seeat.presentation.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.databinding.PostItemBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.common.ViewHolder
import com.pob.seeat.utils.KoreanMatcher
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime
import com.pob.seeat.utils.Utils.toTagList

enum class SearchType {
    TITLE, TAG
}

class BottomSheetFeedAdapter(
    private val onClick: (FeedModel) -> Unit,
    private val updateMarker: (List<FeedModel>) -> Unit,
    private val handleEmptyFeedList: (Int) -> Unit,
) :
    ListAdapter<FeedModel, ViewHolder<FeedModel>>(object : DiffUtil.ItemCallback<FeedModel>() {
        override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem.feedId == newItem.feedId
        }

        override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            return oldItem == newItem
        }
    }), Filterable {
    class PostViewHolder(val binding: PostItemBinding, private val onClick: (FeedModel) -> Unit) :
        ViewHolder<FeedModel>(binding.root) {
        override fun onBind(item: FeedModel) = with(binding) {

            if (item.contentImage.isNotEmpty()) {
                Glide.with(itemView.context).load(item.contentImage[0]).into(ivPostMainImage)
            } else {
                ivPostMainImage.visibility = View.GONE
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

            initTag(item.tags, chipsGroupMainFeed)
        }

        private fun initTag(tags: List<String>, chipsGroupMainFeed: ChipGroup) {
            chipsGroupMainFeed.removeAllViews() // 기존의 Chip들을 모두 제거
            val tagLists = tags.toTagList()
            // tagList를 이용해 Chip을 동적으로 생성
            // tagLists:List<tag>
            for (tag in tagLists) {
                val chip = Chip(itemView.context).apply {
                    text = tag.tagName
                    setChipIconResource(tag.tagImage)

                    chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.white)
                    chipStrokeWidth = 0f
                    chipIconSize = 16f.px.toFloat()
                    chipCornerRadius = 32f.px.toFloat()
                    chipStartPadding = 10f.px.toFloat()

                    elevation = 2f.px.toFloat()

                    isCheckable = false
                    isClickable = false
                }

                // ChipGroup에 동적으로 Chip 추가
                chipsGroupMainFeed.addView(chip)
            }
        }

    }

    private var originalList: List<FeedModel> = emptyList()
    private val searchQuery = hashMapOf<SearchType, CharSequence?>(
        SearchType.TITLE to null,
        SearchType.TAG to null,
    )

    override fun submitList(list: List<FeedModel>?) {
        super.submitList(list)
        originalList = originalList.ifEmpty { list ?: emptyList() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<FeedModel> {
        return PostViewHolder(
            PostItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<FeedModel>, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {
                    values = if (searchQuery.all { it.value.isNullOrBlank() }) {
                        originalList
                    } else {
                        val tagFiltered = filterTag(originalList)
                        val titleFiltered = filterTitle(tagFiltered)
                        titleFiltered
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (searchQuery.all { it.value.isNullOrBlank() }) {
                    handleEmptyFeedList(originalList.size)
                    updateMarker(originalList)
                    submitList(originalList)
                } else {
                    handleEmptyFeedList((results?.values as? List<FeedModel>)?.size ?: 0)
                    updateMarker(results?.values as? List<FeedModel> ?: emptyList())
                    submitList(results?.values as? List<FeedModel>)
                }
            }
        }
    }

    private fun filterTitle(list: List<FeedModel>): List<FeedModel> {
        val target = searchQuery[SearchType.TITLE].toString()
        return if (target.isNotBlank() && target != "null") list.filter { item ->
            if (target.all { it in 'a'..'z' || it in 'A'..'Z' }) {
                item.title.lowercase().contains(target.lowercase())
            } else {
                KoreanMatcher.matchKoreanAndConsonant(item.title, target)
            }
        } else list
    }

    private fun filterTag(list: List<FeedModel>): List<FeedModel> {
        val target = searchQuery[SearchType.TAG].toString()
        return if (target.isNotBlank() && target != "null") list.filter { item ->
            item.tags.contains(target)
        } else list
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        originalList = emptyList()
        searchQuery[SearchType.TITLE] = null
        searchQuery[SearchType.TAG] = null
    }

    fun performSearch(type: SearchType, query: CharSequence?) {
        searchQuery[type] = query
        filter.filter(query)
    }
}

