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
import com.pob.seeat.presentation.model.Alarm

class AlarmAdapter(private val onClick: (Alarm) -> Unit) :
    ListAdapter<Alarm, AlarmAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem == newItem
    }) {
    class ViewHolder(binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvAlarmTitle
        private val description = binding.tvAlarmDescription
        private val content = binding.tvAlarmContent
        private val time = binding.tvAlarmTime
        private val image = binding.ivAlarmImage

        fun bind(alarm: Alarm, onClick: (Alarm) -> Unit) {
            if (alarm.type == "like") {
                content.visibility = View.INVISIBLE
                description.text = itemView.context.getString(R.string.alarm_description_like, "아무개")
            } else {
                content.visibility = View.VISIBLE
                description.text = itemView.context.getString(R.string.alarm_description_comment)
            }
            title.text = itemView.context.getString(R.string.alarm_post_title, alarm.title)
            content.text = alarm.content
            time.text = alarm.time
            Glide.with(image.context)
                .load(alarm.image)
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