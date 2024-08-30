package com.pob.seeat.presentation.view.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemAlarmBinding
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.presentation.common.ViewHolder
import com.pob.seeat.utils.Utils.toKoreanDiffString

class AlarmAdapter(private val onClick: (AlarmModel) -> Unit) :
    ListAdapter<AlarmModel, ViewHolder<AlarmModel>>(object : DiffUtil.ItemCallback<AlarmModel>() {
        override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem.uId == newItem.uId

        override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem == newItem
    }) {
    class Holder(binding: ItemAlarmBinding) : ViewHolder<AlarmModel>(binding.root) {
        private val title = binding.tvAlarmTitle
        private val description = binding.tvAlarmDescription
        private val content = binding.tvAlarmContent
        private val time = binding.tvAlarmTime
        private val image = binding.ivAlarmImage

        override fun bind(item: AlarmModel, onClick: (AlarmModel) -> Unit) {
            content.visibility = View.VISIBLE
            description.text = itemView.context.getString(R.string.alarm_description_comment)
            title.text = itemView.context.getString(R.string.alarm_post_title, item.postTitle)
            content.text = item.content
            time.text = item.createdAt.toKoreanDiffString()
            Glide.with(image.context)
                .load(item.postImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(image)
            itemView.apply {
                val bgColor =
                    itemView.context.getColor(if (item.isRead) R.color.white else R.color.tertiary)
                setBackgroundColor(bgColor)
                background.alpha = if (item.isRead) 255 else 100
                setOnClickListener { onClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<AlarmModel>, position: Int) {
        holder.bind(getItem(position), onClick)
    }
}