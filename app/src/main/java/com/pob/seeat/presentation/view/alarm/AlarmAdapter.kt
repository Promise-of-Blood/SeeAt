package com.pob.seeat.presentation.view.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemAlarmBinding
import com.pob.seeat.domain.model.AlarmModel

class AlarmAdapter(private val onClick: (AlarmModel) -> Unit) :
    ListAdapter<AlarmModel, AlarmAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AlarmModel>() {
        override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem.uId == newItem.uId

        override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel) =
            oldItem == newItem
    }) {
    class ViewHolder(binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvAlarmTitle
        private val description = binding.tvAlarmDescription
        private val content = binding.tvAlarmContent
        private val time = binding.tvAlarmTime
        private val image = binding.ivAlarmImage

        fun bind(alarm: AlarmModel, onClick: (AlarmModel) -> Unit) {
            content.visibility = View.VISIBLE
            description.text = itemView.context.getString(R.string.alarm_description_comment)
            title.text = itemView.context.getString(R.string.alarm_post_title, alarm.postTitle)
            content.text = alarm.content
            time.text = alarm.createdAt.toString()
            Glide.with(image.context)
                .load(alarm.postImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(image)
            itemView.apply {
                val bgColor =
                    itemView.context.getColor(if (alarm.isRead) R.color.white else R.color.tertiary)
                setBackgroundColor(bgColor)
                background.alpha = if (alarm.isRead) 255 else 100
                setOnClickListener { onClick(alarm) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }
}