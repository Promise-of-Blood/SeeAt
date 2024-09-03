package com.pob.seeat.presentation.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pob.seeat.databinding.ItemCommentBinding
import com.pob.seeat.databinding.PostItemBinding
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.presentation.view.common.ViewHolder
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

class FeedCommentAdapter(private val onClick: (CommentModel) -> Unit) :
    ListAdapter<CommentModel, ViewHolder<CommentModel>>(object : DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }


    }) {
    class PostViewHolder(val binding: ItemCommentBinding, private val onClick: (CommentModel) -> Unit) :
        ViewHolder<CommentModel>(binding.root) {
        override fun onBind(item: CommentModel) = with(binding) {
            // Todo 유저이미지 연결
//            ivCommentItemUserImage.setImageResource(item.user)
//            tvCommentItemUsername.text = item.user
            tvCommentItemContent.text = item.comment
            clCommentLayout.setOnClickListener {
                onClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<CommentModel> {
        return PostViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<CommentModel>, position: Int) {
        holder.onBind(getItem(position))
    }

}