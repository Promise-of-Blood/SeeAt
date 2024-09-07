package com.pob.seeat.presentation.view.feed

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pob.seeat.databinding.ItemImageUploadBinding

class ImageUploadAdapter(
    private val onClick: (Uri) -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<Uri, ImageUploadAdapter.ImageViewHolder>(object : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }
}) {

    class ImageViewHolder(
        private val binding: ItemImageUploadBinding,
        private val onClick: (Uri) -> Unit,
        private val onDelete: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Uri) {
            // 이미지 로드
            Glide.with(itemView.context)
                .load(item)
                .override(800, 800)
                .into(binding.ivImage)

            // 이미지 클릭 시 처리
            binding.ivImage.setOnClickListener {
                onClick(item)
            }

            // 삭제 버튼 클릭 시 처리
            binding.ivDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDelete(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemImageUploadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick,
            onDelete
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
